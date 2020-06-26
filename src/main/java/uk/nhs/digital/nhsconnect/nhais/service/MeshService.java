package uk.nhs.digital.nhsconnect.nhais.service;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

import org.apache.qpid.jms.message.JmsBytesMessage;
import org.apache.qpid.jms.message.JmsTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component @Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeshService {

    private final ObjectMapper objectMapper;

    private final MeshClient meshClient;

    private final MeshConfig meshConfig;

//    @JmsListener(destination = "${nhais.amqp.meshOutboundQueueName}")
//    public void sendMeshMessage(Message message) throws IOException, JMSException {
//        LOGGER.debug("Received message: {}", message);
//        try {
//            String body = readMessage(message);
//            LOGGER.debug("Received message body: {}", body);
//            MeshMessage meshMessage = objectMapper.readValue(body, MeshMessage.class);
//            LOGGER.debug("Decoded message: {}", meshMessage);
//            // TODO: get the correlation id and attach to logger?
//
//            meshClient.sendEdifactMessage(meshMessage.getContent(), meshConfig.getMailboxId());
//
//        } catch (Exception e) {
//            LOGGER.error("Error while processing mesh inbound queue message", e);
//            // TODO: deadletter if something goes pop instead of throwing exception
//            throw e;
//        }
//    }

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
