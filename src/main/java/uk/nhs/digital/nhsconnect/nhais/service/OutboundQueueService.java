package uk.nhs.digital.nhsconnect.nhais.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OutboundQueueService {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final TimestampService timestampService;
    private final MeshClient meshClient;

    @Value("${nhais.amqp.meshOutboundQueueName}")
    private String meshOutboundQueueName;

    @SneakyThrows
    public void publish(OutboundMeshMessage message) {
        message.setMessageSentTimestamp(timestampService.formatInISO(timestampService.getCurrentTimestamp()));
        jmsTemplate.send(meshOutboundQueueName, session -> session.createTextMessage(serializeMeshMessage(message)));
    }

    @SneakyThrows
    private String serializeMeshMessage(OutboundMeshMessage meshMessage) {
        return objectMapper.writeValueAsString(meshMessage);
    }

//    @JmsListener(destination = "${nhais.amqp.meshOutboundQueueName}") //TODO: enable for NIAD-122 Sending MESH
    public void receive(Message message) throws IOException, JMSException {
        LOGGER.debug("Received message: {}", message);
        try {
            String body = JmsReader.readMessage(message);
            LOGGER.debug("Received message body: {}", body);
            OutboundMeshMessage meshMessage = objectMapper.readValue(body, OutboundMeshMessage.class);
            LOGGER.debug("Decoded message: {}", meshMessage);
            // TODO: get the correlation id and attach to logger?
//            String recipient = meshCypherDecoder.getRecipient(meshMessage);
//            meshClient.sendEdifactMessage(meshMessage.getContent(), recipient);

        } catch (Exception e) {
            LOGGER.error("Error while processing mesh inbound queue message", e);
            // TODO: deadletter if something goes pop instead of throwing exception
            throw e;
        }
    }

}
