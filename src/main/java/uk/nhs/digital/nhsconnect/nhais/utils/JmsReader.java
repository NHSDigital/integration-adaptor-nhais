package uk.nhs.digital.nhsconnect.nhais.utils;

import org.apache.qpid.jms.message.JmsBytesMessage;
import org.apache.qpid.jms.message.JmsTextMessage;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

public class JmsReader {

    public static String readMessage(Message message) throws JMSException {
        if (message instanceof JmsTextMessage jmsTextMessage) {
            return readTextMessage(jmsTextMessage);
        }
        if (message instanceof JmsBytesMessage jmsBytesMessage) {
            return readBytesMessage(jmsBytesMessage);
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
