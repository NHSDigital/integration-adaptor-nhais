package uk.nhs.digital.nhsconnect.nhais.jms;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import javax.jms.JMSException;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
public class DeadLetterQueueTest extends MeshServiceBaseTest {

    private static final String MESSAGE_CONTENT = "TRASH";

    @Test
    public void whenSendingInvalidMessage_thenMessageIsSentToDeadLetterQueue() throws JMSException {
        sendToMeshInboundQueue(MESSAGE_CONTENT);

        var message = getDeadLetterInboundQueueMessage();
        var messageBody = parseTextMessage(message);

        assertThat(messageBody).isEqualTo(MESSAGE_CONTENT);
    }
}
