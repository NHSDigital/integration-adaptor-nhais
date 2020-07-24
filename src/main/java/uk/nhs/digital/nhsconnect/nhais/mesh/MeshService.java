package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.inbound.queue.InboundQueueService;

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
        LOGGER.debug("Trying to scan MESH mailbox");
        if (!meshMailBoxScheduler.isEnabled()){
            LOGGER.warn("MESH mailbox scheduler is disabled. Set proper env var to enable it");
            return;
        }
        if (meshMailBoxScheduler.hasTimePassed(scanDelayInSeconds)) {
            LOGGER.info("Mesh messages scan started");
            List<String> inboxMessageIds = downloadMessageIds();
            for (String messageId : inboxMessageIds) {
                try {
                    InboundMeshMessage meshMessage = meshClient.getEdifactMessage(messageId);
                    inboundQueueService.publish(meshMessage);
                    meshClient.acknowledgeMessage(meshMessage.getMeshMessageId());
                } catch (Exception ex) {
                    LOGGER.error("Error during reading of MESH message. Message id: {}", messageId, ex);
                    //ignore exception and try to download next message
                }
            }
            LOGGER.info("Mesh mailbox scanning finished");
        } else {
            LOGGER.info("Can't scan MESH mailbox - scan delay time hasn't passed yet or another instance did the scan. " +
                "Next scan in {} seconds", scanDelayInSeconds);
        }
    }

    private List<String> downloadMessageIds() {
        List<String> inboxMessageIds = meshClient.getInboxMessageIds();
        if(inboxMessageIds.isEmpty()){
            LOGGER.info("No new MESH messages found");
        } else {
            LOGGER.info("Found {} MESH message(s) in inbox", inboxMessageIds.size());
        }
        return inboxMessageIds;
    }
}
