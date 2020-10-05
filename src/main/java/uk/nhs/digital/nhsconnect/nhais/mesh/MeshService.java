package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.queue.InboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MeshService {

    private final MeshClient meshClient;

    private final InboundQueueService inboundQueueService;

    private final MeshMailBoxScheduler meshMailBoxScheduler;

    private final ConversationIdService conversationIdService;

    private final long pollingCycleMinimumIntervalInSeconds;

    private final long wakeupIntervalInMilliseconds;

    private final long pollingCycleDurationInSeconds;

    @Autowired
    public MeshService(MeshClient meshClient,
                       InboundQueueService inboundQueueService,
                       MeshMailBoxScheduler meshMailBoxScheduler,
                       ConversationIdService conversationIdService,
                       @Value("${nhais.mesh.pollingCycleMinimumIntervalInSeconds}") long pollingCycleMinimumIntervalInSeconds,
                       @Value("${nhais.mesh.wakeupIntervalInMilliseconds}") long wakeupIntervalInMilliseconds,
                       @Value("${nhais.mesh.pollingCycleDurationInSeconds}") long pollingCycleDurationInSeconds) {
        this.meshClient = meshClient;
        this.inboundQueueService = inboundQueueService;
        this.meshMailBoxScheduler = meshMailBoxScheduler;
        this.conversationIdService = conversationIdService;
        this.pollingCycleMinimumIntervalInSeconds = pollingCycleMinimumIntervalInSeconds;
        this.wakeupIntervalInMilliseconds = wakeupIntervalInMilliseconds;
        this.pollingCycleDurationInSeconds = pollingCycleDurationInSeconds;
    }

    @Scheduled(fixedRateString = "${nhais.mesh.wakeupIntervalInMilliseconds}")
    public void scanMeshInboxForMessages() {
        if (!meshMailBoxScheduler.isEnabled()){
            LOGGER.warn("Not running the MESH mailbox polling cycle because it is disabled. Set variable " +
                "NHAIS_SCHEDULER_ENABLED to true to enable it.");
            return;
        }
        LOGGER.info("Requesting lock from database to run MESH mailbox polling cycle");
        StopWatch pollingCycleElapsedTime = new StopWatch();
        pollingCycleElapsedTime.start();
        if (meshMailBoxScheduler.hasTimePassed(pollingCycleMinimumIntervalInSeconds)) {
            List<String> inboxMessageIds = authenticateAndGetInboxMessageIds();
            for (int i = 0; i < inboxMessageIds.size(); i++) {
                String messageId = inboxMessageIds.get(i);
                if(sufficientTimeRemainsInPollingCycle(pollingCycleElapsedTime)) {
                    processSingleMessage(messageId);
                } else {
                    LOGGER.warn("Insufficient time remains to complete the polling cycle. Processed {} of {} messages from inbox.", i + 1, inboxMessageIds.size());
                    return;
                }
            }
            LOGGER.info("Completed MESH mailbox polling cycle. Processed all messages from inbox.");
        } else {
            LOGGER.info("Could not obtain database lock to run MESH mailbox polling cycle: insufficient time has elapsed " +
                "since the previous polling cycle or another adaptor instance has already started the polling cycle. " +
                "Next scan in {} seconds", TimeUnit.SECONDS.convert(wakeupIntervalInMilliseconds, TimeUnit.MILLISECONDS));
        }
    }

    private List<String> authenticateAndGetInboxMessageIds() {
        LOGGER.info("Starting MESH mailbox polling cycle");
        meshClient.authenticate();
        LOGGER.info("Authenticated with MESH API at start of polling cycle");
        List<String> inboxMessageIds = meshClient.getInboxMessageIds();
        LOGGER.info("There are {} messages in the MESH mailbox", inboxMessageIds.size());
        return inboxMessageIds;
    }

    private boolean sufficientTimeRemainsInPollingCycle(StopWatch stopWatch) {
        return stopWatch.getTime(TimeUnit.SECONDS) < pollingCycleDurationInSeconds;
    }

    private void processSingleMessage(String messageId) {
        try {
            conversationIdService.applyConversationId(messageId);
            LOGGER.debug("Downloading message id {}", messageId);
            InboundMeshMessage meshMessage = meshClient.getEdifactMessage(messageId);
            LOGGER.debug("Publishing content of message id {} to inbound mesh MQ", messageId);
            inboundQueueService.publish(meshMessage);
            LOGGER.debug("Acknowledging message id {} on MESH API", messageId);
            meshClient.acknowledgeMessage(meshMessage.getMeshMessageId());
        } catch (MeshWorkflowUnknownException ex) {
            LOGGER.warn("Message id {} has an unsupported workflow id {} and has been left in the inbox.", messageId, ex.getWorkflowId());
        } catch (Exception ex) {
            LOGGER.error("Error during reading of MESH message. Message id: {}", messageId, ex);
            // skip message with error and attempt to download the next one
        } finally {
            conversationIdService.resetConversationId();
        }
    }

}
