package uk.nhs.digital.nhsconnect.nhais.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.qpid.jms.message.JmsBytesMessage;
import org.apache.qpid.jms.message.JmsTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.UnknownWorkflowException;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;

@Component @Slf4j
public class InboundMeshService {

    @Autowired
    private RegistrationConsumerService registrationConsumerService;

    @Autowired
    private RecepConsumerService recepConsumerService;

    @Autowired
    private ObjectMapper objectMapper;

    @JmsListener(destination = "${nhais.amqp.meshInboundQueueName}")
    public void handleInboundMessage(Message message) throws IOException, JMSException {
        LOGGER.debug("Received message: {}", message);
        try {
            String body = readMessage(message);
            LOGGER.debug("Received message body: {}", body);
            MeshMessage meshMessage = objectMapper.readValue(body, MeshMessage.class);
            LOGGER.debug("Decoded message: {}", meshMessage);
            // TODO: get the correlation id and attach to logger?

            if (WorkflowId.REGISTRATION.equals(meshMessage.getWorkflowId())) {
                registrationConsumerService.handleRegistration(meshMessage);
            } else if (WorkflowId.RECEP.equals(meshMessage.getWorkflowId())) {
                recepConsumerService.handleRecep(meshMessage);
            } else {
                throw new UnknownWorkflowException(meshMessage.getWorkflowId());
            }

            message.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error while processing mesh inbound queue message", e);
            // TODO: deadletter if something goes pop instead of throwing exception
            throw e;
        }
    }

    public static String readMessage(Message message) throws JMSException {
        if (message instanceof JmsTextMessage) {
            return readTextMessage((JmsTextMessage) message);
        }
        if (message instanceof JmsBytesMessage) {
            return readBytesMessage((JmsBytesMessage) message);
        }
        if (message != null) {
            return message.getBody(String.class);
        }
        return null;
    }

    private static String readBytesMessage(JmsBytesMessage message) throws JMSException {
        byte[] bytes = new byte[(int) message.getBodyLength()];
        message.readBytes(bytes);
        return new String(bytes);
    }

    private static String readTextMessage(JmsTextMessage message) throws JMSException {
        return message.getText();
    }
}
