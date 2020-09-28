package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.IntegrationBaseTest;

import javax.jms.JMSException;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
public class DeadLetterQueueTest extends IntegrationBaseTest {

    private static final String MESSAGE_CONTENT = "TRASH";

    @Test
    public void whenSendingInvalidMessage_thenMessageIsSentToDeadLetterQueue() throws JMSException {
        sendToMeshInboundQueue(MESSAGE_CONTENT);

        var message = getDeadLetterMeshInboundQueueMessage();
        var messageBody = parseTextMessage(message);

        assertThat(messageBody).isEqualTo(MESSAGE_CONTENT);
    }
}
