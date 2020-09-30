package uk.nhs.digital.nhsconnect.nhais.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
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

    @Value("${nhais.amqp.meshOutboundQueueName}")
    private String meshOutboundQueueName;

    @SneakyThrows
    public void publish(OutboundMeshMessage messageContent) {
        LOGGER.info("Publishing message to MESH outbound queue for synchronous sending to MESH API OperationId={} recipient={} workflow={}",
            messageContent.getOperationId(), messageContent.getHaTradingPartnerCode(), messageContent.getWorkflowId());
        LOGGER.debug("Publishing message to outbound mesh queue: {}", messageContent);
        messageContent.setMessageSentTimestamp(timestampService.formatInISO(timestampService.getCurrentTimestamp()));
        jmsTemplate.send(meshOutboundQueueName, session -> {
            var message = session.createTextMessage(serializeMeshMessage(messageContent));
            message.setStringProperty(JmsHeaders.CORRELATION_ID, MDC.get(CorrelationIdFilter.KEY));
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
            setLoggingCorrelationId(message);
            LOGGER.info("Consuming message from outbound MESH message queue");
            String body = JmsReader.readMessage(message);
            OutboundMeshMessage outboundMeshMessage = objectMapper.readValue(body, OutboundMeshMessage.class);
            LOGGER.debug("Parsed message into object: {}", outboundMeshMessage);
            meshClient.authenticate();
            meshClient.sendEdifactMessage(outboundMeshMessage);
        } catch (Exception e) {
            LOGGER.error("Error while processing mesh inbound queue message", e);
            throw e; // rethrow so message will be sent to DLQ after a few unsuccessful deliveries
        } finally {
            clearLoggingCorrelationId();
        }
    }

    private void setLoggingCorrelationId(Message message) {
        try {
            MDC.put(CorrelationIdFilter.KEY, message.getStringProperty(JmsHeaders.CORRELATION_ID));
        } catch (JMSException e) {
            LOGGER.error("Unable to read header " + JmsHeaders.CORRELATION_ID + " from message", e);
        }
    }

    /**
     * Must be called from a finally for the try in which setLoggingCorrelationId is called to ensure the value is
     * always cleared after processing the message
     */
    private void clearLoggingCorrelationId() {
        MDC.remove(CorrelationIdFilter.KEY);
    }
}
