package uk.nhs.digital.nhsconnect.nhais.inbound.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.RecepConsumerService;
import uk.nhs.digital.nhsconnect.nhais.inbound.RegistrationConsumerService;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import javax.jms.Message;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
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
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private InboundQueueService inboundQueueService;

    @Mock
    private Message message;

    @Test
    public void registrationMessage_handledByRegistrationConsumerService() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_REG\"}");

        inboundQueueService.receive(message);

        MeshMessage expectedMeshMessage = new MeshMessage();
        expectedMeshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        verify(registrationConsumerService).handleRegistration(expectedMeshMessage);
        verify(message).acknowledge();
    }

    @Test
    public void registrationMessage_registrationConsumerServiceThrowsException_noAck() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_REG\"}");
        doThrow(RuntimeException.class).when(registrationConsumerService).handleRegistration(any(MeshMessage.class));

        assertThrows(RuntimeException.class, () -> inboundQueueService.receive(message));

        verify(message, times(0)).acknowledge();
    }

    @Test
    public void recepMessage_handledByRecepConsumerService() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_RECEP\"}");

        inboundQueueService.receive(message);

        MeshMessage expectedMeshMessage = new MeshMessage();
        expectedMeshMessage.setWorkflowId(WorkflowId.RECEP);
        verify(recepConsumerService).handleRecep(expectedMeshMessage);
        verify(message).acknowledge();
    }

    @Test
    public void registrationMessage_recepConsumerServiceThrowsException_noAck() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_RECEP\"}");
        doThrow(RuntimeException.class).when(recepConsumerService).handleRecep(any(MeshMessage.class));

        assertThrows(RuntimeException.class, () -> inboundQueueService.receive(message));

        verify(message, times(0)).acknowledge();
    }

    @Test
    public void unknownWorkflow_throwsUnknownWorkflowException_noAck() throws Exception{
        when(message.getBody(String.class)).thenReturn("{}");

        assertThrows(UnknownWorkflowException.class, () -> inboundQueueService.receive(message));

        verifyNoInteractions(recepConsumerService, registrationConsumerService);
        verify(message, times(0)).acknowledge();
    }

}
