package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.inbound.queue.InboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;

import java.util.List;

@Component
@Slf4j
public class MeshService {

    private final MeshClient meshClient;

    private final InboundQueueService inboundQueueService;

    private final MeshMailBoxScheduler meshMailBoxScheduler;

    private final Long scanDelayInSeconds;

    @Autowired
    public MeshService(MeshClient meshClient,
                       InboundQueueService inboundQueueService,
                       MeshMailBoxScheduler meshMailBoxScheduler,
                       @Value("${nhais.mesh.scanMailboxDelayInSeconds}") long scanDelayInSeconds) {
        this.meshClient = meshClient;
        this.inboundQueueService = inboundQueueService;
        this.meshMailBoxScheduler = meshMailBoxScheduler;
        this.scanDelayInSeconds = scanDelayInSeconds;
    }

    @Scheduled(fixedRateString = "${nhais.mesh.scanMailboxIntervalInMilliseconds}")
    public void scanMeshInboxForMessages() {
        if (!meshMailBoxScheduler.isEnabled()){
            LOGGER.warn("Not running the MESH mailbox polling cycle because it is disabled. Set variable " +
                "NHAIS_SCHEDULER_ENABLED to true to enable it.");
            return;
        }
        LOGGER.info("Requesting lock from database to run MESH mailbox polling cycle");
        if (meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)) {
            LOGGER.info("Starting MESH mailbox polling cycle");
            List<String> inboxMessageIds = meshClient.getInboxMessageIds();
            LOGGER.info("There are {} messages in the MESH mailbox", inboxMessageIds.size());
            for (String messageId : inboxMessageIds) {
                try {
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
                    //ignore exception and try to download next message
                }
            }
            LOGGER.info("Completed MESH mailbox polling cycle");
        } else {
            LOGGER.info("Could not obtain database lock to run MESH mailbox polling cycle: insufficient time as elapsed " +
                "since the previous polling cycle or another adaptor instance has already started the polling cycle.");
        }
    }

}
