package uk.nhs.digital.nhsconnect.nhais.mesh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.EdifactToMeshMessageService;
import uk.nhs.digital.nhsconnect.nhais.service.InboundQueueService;

import java.util.List;

@Component
@Slf4j
public class MeshService {

    private final MeshClient meshClient;

    private final EdifactToMeshMessageService edifactToMeshMessageService;

    private final InboundQueueService inboundQueueService;

    private final MeshMailBoxScheduler meshMailBoxScheduler;

    private final Long intervalInSeconds;

    @Autowired
    public MeshService(MeshClient meshClient,
                       EdifactToMeshMessageService edifactToMeshMessageService,
                       InboundQueueService inboundQueueService,
                       MeshMailBoxScheduler meshMailBoxScheduler,
                       @Value("${nhais.mesh.scanMailboxDelayInSeconds}") long intervalInSeconds) {
        this.meshClient = meshClient;
        this.edifactToMeshMessageService = edifactToMeshMessageService;
        this.inboundQueueService = inboundQueueService;
        this.meshMailBoxScheduler = meshMailBoxScheduler;
        this.intervalInSeconds = intervalInSeconds;
    }

    @Scheduled(fixedRateString = "${nhais.mesh.scanMailboxIntervalInMilliseconds}")
    public void scanMeshInboxForMessages() {
        LOGGER.debug("Scheduled job for mesh messages fetching started");
        if (meshMailBoxScheduler.hasTimePassed(intervalInSeconds)) {
            LOGGER.info("Mesh messages fetching started");
            List<String> inboxMessageIds = meshClient.getInboxMessageIds();
            if(inboxMessageIds.isEmpty()){
                LOGGER.info("No new MESH messages found");
                return;
            }
            for (String messageId : inboxMessageIds) {
                try {
                    String edifactString = meshClient.getEdifactMessage(messageId);
                    MeshMessage meshMessage = edifactToMeshMessageService.fromEdifactString(edifactString, messageId);
                    inboundQueueService.publish(meshMessage);
                    meshClient.acknowledgeMessage(meshMessage.getMeshMessageId());
                } catch (Exception ex) {
                    LOGGER.error("Error during reading of MESH messages. Message id: {}", messageId, ex);
                }
            }
            LOGGER.info("Mesh messages fetching finished");
        } else {
            LOGGER.info("Mesh messages fetching is postponed: another application instance is fetching now");
        }
    }
}
