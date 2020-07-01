package uk.nhs.digital.nhsconnect.nhais.model.mesh;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.qpid.jms.message.JmsBytesMessage;
import org.apache.qpid.jms.message.JmsTextMessage;

import javax.jms.JMSException;
import javax.jms.Message;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MeshMessage {

    private String odsCode;
    private WorkflowId workflowId;
    private String content;
    /**
     * Correlation id associated with the request - used for distributed tracing
     */
    private String correlationId;
    /**
     * The timestamp (ISO format, UTC) when this message was sent - used for debugging and tracing
     */
    private String messageSentTimestamp;

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
