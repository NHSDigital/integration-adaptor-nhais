package uk.nhs.digital.nhsconnect.nhais.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsReader;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

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
    private final ConversationIdService conversationIdService;

    @Value("${nhais.amqp.meshOutboundQueueName}")
    private String meshOutboundQueueName;

    @SneakyThrows
    public void publish(OutboundMeshMessage messageContent) {
        LOGGER.info("Publishing message to MESH outbound queue for asynchronous sending to MESH API OperationId={} recipient={} workflow={}",
            messageContent.getOperationId(), messageContent.getHaTradingPartnerCode(), messageContent.getWorkflowId());
        LOGGER.debug("Publishing message content to outbound mesh queue: {}", messageContent);
        messageContent.setMessageSentTimestamp(timestampService.formatInISO(timestampService.getCurrentTimestamp()));
        jmsTemplate.send(meshOutboundQueueName, session -> {
            var message = session.createTextMessage(serializeMeshMessage(messageContent));
            message.setStringProperty(JmsHeaders.CONVERSATION_ID, conversationIdService.getCurrentConversationId());
            return message;
        });
    }

    @SneakyThrows
    private String serializeMeshMessage(OutboundMeshMessage meshMessage) {
        return objectMapper.writeValueAsString(meshMessage);
    }

    @JmsListener(destination = "${nhais.amqp.meshOutboundQueueName}")
    public void receive(Message message) throws IOException, JMSException {
        try {
            setLoggingConversationId(message);
            LOGGER.info("Consuming message from outbound MESH message queue");
            String body = JmsReader.readMessage(message);
            OutboundMeshMessage outboundMeshMessage = objectMapper.readValue(body, OutboundMeshMessage.class);
            LOGGER.debug("Parsed message into object: {}", outboundMeshMessage);
            meshClient.authenticate();
            meshClient.sendEdifactMessage(outboundMeshMessage);
        } catch (Exception e) {
            LOGGER.error("Error while processing mesh outbound queue message", e);
            throw e; // rethrow so message will be sent to DLQ after a few unsuccessful deliveries
        } finally {
            conversationIdService.resetConversationId();
        }
    }

    private void setLoggingConversationId(Message message) {
        try {
            conversationIdService.applyConversationId(message.getStringProperty(JmsHeaders.CONVERSATION_ID));
        } catch (JMSException e) {
            LOGGER.error("Unable to read header " + JmsHeaders.CONVERSATION_ID + " from message", e);
        }
    }
}
