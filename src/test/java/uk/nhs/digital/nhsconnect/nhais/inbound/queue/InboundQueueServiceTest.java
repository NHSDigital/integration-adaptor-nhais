package uk.nhs.digital.nhsconnect.nhais.inbound.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import uk.nhs.digital.nhsconnect.nhais.inbound.RecepConsumerService;
import uk.nhs.digital.nhsconnect.nhais.inbound.RegistrationConsumerService;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InboundQueueServiceTest {

    @Mock
    private RegistrationConsumerService registrationConsumerService;

    @Mock
    private RecepConsumerService recepConsumerService;

    @Spy
    private ObjectMapper objectMapper;

    @Spy
    private TimestampService timestampService;

    @Mock
    private ConversationIdService conversationIdService;

    @Captor
    private ArgumentCaptor<MessageCreator> jmsMessageCreatorCaptor;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private InboundQueueService inboundQueueService;

    @Mock
    private Message message;

    @Test
    public void when_receive_registrationMessage_then_handledByRegistrationConsumerService() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_REG\"}");

        inboundQueueService.receive(message);

        MeshMessage expectedMeshMessage = new MeshMessage();
        expectedMeshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        verify(registrationConsumerService).handleRegistration(expectedMeshMessage);
        verify(message).acknowledge();
    }

    @Test
    public void when_receive_registrationMessage_registrationConsumerServiceThrowsException_noAck() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_REG\"}");
        doThrow(RuntimeException.class).when(registrationConsumerService).handleRegistration(any(MeshMessage.class));

        assertThrows(RuntimeException.class, () -> inboundQueueService.receive(message));

        verify(message, times(0)).acknowledge();
    }

    @Test
    public void when_receive_recepMessage_handledByRecepConsumerService() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_RECEP\"}");

        inboundQueueService.receive(message);

        MeshMessage expectedMeshMessage = new MeshMessage();
        expectedMeshMessage.setWorkflowId(WorkflowId.RECEP);
        verify(recepConsumerService).handleRecep(expectedMeshMessage);
        verify(message).acknowledge();
    }

    @Test
    public void when_receive_registrationMessage_recepConsumerServiceThrowsException_noAck() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_RECEP\"}");
        doThrow(RuntimeException.class).when(recepConsumerService).handleRecep(any(MeshMessage.class));

        assertThrows(RuntimeException.class, () -> inboundQueueService.receive(message));

        verify(message, times(0)).acknowledge();
    }

    @Test
    public void when_receive_unknownWorkflow_throwsUnknownWorkflowException_noAck() throws Exception{
        when(message.getBody(String.class)).thenReturn("{}");

        assertThrows(UnknownWorkflowException.class, () -> inboundQueueService.receive(message));

        verifyNoInteractions(recepConsumerService, registrationConsumerService);
        verify(message, times(0)).acknowledge();
    }

    @Test
    public void when_publish_inboundMessageFromMesh_thenTimestampAndConversationIdAreSet() throws Exception {
        final var now = Instant.now();
        when(timestampService.getCurrentTimestamp()).thenReturn(now);
        final var messageSentTimestamp = "2020-06-12T14:15:16Z";
        when(timestampService.formatInISO(now)).thenReturn(messageSentTimestamp);
        final var conversationId = "CONV123";
        when(conversationIdService.getCurrentConversationId()).thenReturn(conversationId);

        InboundMeshMessage inboundMeshMessage = InboundMeshMessage.create(WorkflowId.REGISTRATION, "ASDF", null, "ID123");

        inboundQueueService.publish(inboundMeshMessage);

        // the method parameter is modified so another copy is needed. Timestamp set to expected value
        InboundMeshMessage expectedInboundMeshMessage = InboundMeshMessage.create(WorkflowId.REGISTRATION, "ASDF", messageSentTimestamp, "ID123");
        String expectedStringMessage = objectMapper.writeValueAsString(expectedInboundMeshMessage);
        verify(jmsTemplate).send(org.mockito.Mockito.<String>isNull(), jmsMessageCreatorCaptor.capture());
        MessageCreator messageCreator = jmsMessageCreatorCaptor.getValue();
        Session jmsSession = mock(Session.class);
        TextMessage textMessage = mock(TextMessage.class);
        // should not return a testMessage unless timestamp was set to expected value
        when(jmsSession.createTextMessage(expectedStringMessage)).thenReturn(textMessage);

        var actualTextMessage = messageCreator.createMessage(jmsSession);
        assertThat(actualTextMessage).isSameAs(textMessage);
        verify(textMessage).setStringProperty(JmsHeaders.CONVERSATION_ID, conversationId);


    }

}
