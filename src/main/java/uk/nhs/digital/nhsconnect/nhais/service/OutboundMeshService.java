package uk.nhs.digital.nhsconnect.nhais.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OutboundMeshService {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final TimestampService timestampService;

    @Value("${nhais.amqp.meshOutboundQueueName}")
    private String meshOutboundQueueName;

    @SneakyThrows
    public void publishToOutboundQueue(MeshMessage message) {
        message.setMessageSentTimestamp(timestampService.formatInISO(timestampService.getCurrentTimestamp()));
        jmsTemplate.send(meshOutboundQueueName, session -> session.createTextMessage(serializeMeshMessage(message)));
    }

    @SneakyThrows
    private String serializeMeshMessage(MeshMessage meshMessage) {
        return objectMapper.writeValueAsString(meshMessage);
    }
}
