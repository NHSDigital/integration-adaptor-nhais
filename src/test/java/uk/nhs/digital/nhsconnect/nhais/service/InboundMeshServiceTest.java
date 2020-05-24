package uk.nhs.digital.nhsconnect.nhais.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.exceptions.UnknownWorkflowException;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import javax.jms.Message;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InboundMeshServiceTest {

    @Mock
    RegistrationConsumerService registrationConsumerService;

    @Mock
    RecepConsumerService recepConsumerService;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    InboundMeshService inboundMeshService;

    @Mock
    Message message;

    @Test
    public void registrationMessage_handledByRegistrationConsumerService() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_REG\"}");

        inboundMeshService.handleInboundMessage(message);

        MeshMessage expectedMeshMessage = new MeshMessage();
        expectedMeshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        verify(registrationConsumerService).handleRegistration(expectedMeshMessage);
        verify(message).acknowledge();
    }

    @Test
    public void registrationMessage_registrationConsumerServiceThrowsException_noAck() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_REG\"}");
        doThrow(RuntimeException.class).when(registrationConsumerService).handleRegistration(any(MeshMessage.class));

        assertThrows(RuntimeException.class, () -> inboundMeshService.handleInboundMessage(message));

        verify(message, times(0)).acknowledge();
    }

    @Test
    public void recepMessage_handledByRecepConsumerService() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_RECEP\"}");

        inboundMeshService.handleInboundMessage(message);

        MeshMessage expectedMeshMessage = new MeshMessage();
        expectedMeshMessage.setWorkflowId(WorkflowId.RECEP);
        verify(recepConsumerService).handleRecep(expectedMeshMessage);
        verify(message).acknowledge();
    }

    @Test
    public void registrationMessage_recepConsumerServiceThrowsException_noAck() throws Exception {
        when(message.getBody(String.class)).thenReturn("{\"workflowId\":\"NHAIS_RECEP\"}");
        doThrow(RuntimeException.class).when(recepConsumerService).handleRecep(any(MeshMessage.class));

        assertThrows(RuntimeException.class, () -> inboundMeshService.handleInboundMessage(message));

        verify(message, times(0)).acknowledge();
    }

    @Test
    public void unknownWorkflow_throwsUnknownWorkflowException_noAck() throws Exception{
        when(message.getBody(String.class)).thenReturn("{}");

        assertThrows(UnknownWorkflowException.class, () -> inboundMeshService.handleInboundMessage(message));

        verifyNoInteractions(recepConsumerService, registrationConsumerService);
        verify(message, times(0)).acknowledge();
    }

}
