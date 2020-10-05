package uk.nhs.digital.nhsconnect.nhais.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.IntegrationBaseTest;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;

import javax.jms.JMSException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@DirtiesContext
public class DeadLetterQueueTest extends IntegrationBaseTest {

    private static final String MESSAGE_CONTENT = "TRASH";

    @Autowired
    private OutboundQueueService outboundQueueService;

    @SpyBean
    private MeshClient meshClient;

    @SpyBean
    private ConversationIdService conversationIdService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void whenSendingInvalidMessage_toMeshInboundQueue_thenMessageIsSentToDeadLetterQueue() throws JMSException {
        clearDeadLetterQueue(meshInboundQueueName);
        sendToMeshInboundQueue(MESSAGE_CONTENT);

        var message = getDeadLetterMeshInboundQueueMessage(meshInboundQueueName);
        var messageBody = parseTextMessage(message);

        assertThat(messageBody).isEqualTo(MESSAGE_CONTENT);
    }

    @Test
    public void whenMeshOutboundQueueMessageCannotBeProcessed_thenMessageIsSentToDeadLetterQueue() throws Exception {
        String conversationId = Long.toString(System.currentTimeMillis());
        when(conversationIdService.getCurrentConversationId()).thenReturn(conversationId);
        OutboundMeshMessage meshMessage = OutboundMeshMessage.create("XX11", WorkflowId.REGISTRATION, MESSAGE_CONTENT, "2020-01-01T00:00:00Z", "asdf");
        doThrow(RuntimeException.class).when(meshClient).authenticate();

        clearDeadLetterQueue(meshOutboundQueueName);
        outboundQueueService.publish(meshMessage);

        var message = getDeadLetterMeshInboundQueueMessage(meshOutboundQueueName);

        assertThat(message.getStringProperty(JmsHeaders.CONVERSATION_ID)).isEqualTo(conversationId);
        assertThat(parseTextMessage(message)).isEqualTo(objectMapper.writeValueAsString(meshMessage));
    }

}
