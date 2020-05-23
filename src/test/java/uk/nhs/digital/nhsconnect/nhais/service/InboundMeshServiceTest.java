package uk.nhs.digital.nhsconnect.nhais.service;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import uk.nhs.digital.nhsconnect.nhais.model.exception.UnknownWorkflowException;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InboundMeshServiceTest {

    @Mock
    RegistrationConsumerService registrationConsumerService;

    @Mock
    RecepConsumerService recepConsumerService;

    @InjectMocks
    InboundMeshService inboundMeshService;

    @Mock
    Message<MeshMessage> message;

    @Mock
    Channel channel;

    @Test
    public void registrationMessage_handledByRegistrationConsumerService() throws Exception {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        when(message.getPayload()).thenReturn(meshMessage);

        inboundMeshService.handleInboundMessage(message, channel, 1234);

        verify(registrationConsumerService).handleRegistration(meshMessage);
        verify(channel).basicAck(1234, false);
    }

    @Test
    public void registrationMessage_registrationConsumerServiceThrowsException_noAck() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        when(message.getPayload()).thenReturn(meshMessage);
        doThrow(RuntimeException.class).when(registrationConsumerService).handleRegistration(meshMessage);

        assertThrows(RuntimeException.class, () -> inboundMeshService.handleInboundMessage(message, channel, 1234));

        verify(registrationConsumerService).handleRegistration(meshMessage);
        verifyNoInteractions(channel);
    }

    @Test
    public void registrationMessage_handledByRecepConsumerService() throws Exception {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.RECEP);
        when(message.getPayload()).thenReturn(meshMessage);

        inboundMeshService.handleInboundMessage(message, channel, 1234);

        verify(recepConsumerService).handleRecep(meshMessage);
        verify(channel).basicAck(1234, false);
    }

    @Test
    public void registrationMessage_recepConsumerServiceThrowsException_noAck() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.RECEP);
        when(message.getPayload()).thenReturn(meshMessage);
        doThrow(RuntimeException.class).when(recepConsumerService).handleRecep(meshMessage);

        assertThrows(RuntimeException.class, () -> inboundMeshService.handleInboundMessage(message, channel, 1234));

        verify(recepConsumerService).handleRecep(meshMessage);
        verifyNoInteractions(channel);
    }

    @Test
    public void unknownWorkflow_throwsUnknownWorkflowException_noAck() {
        MeshMessage meshMessage = new MeshMessage();
        when(message.getPayload()).thenReturn(meshMessage);

        assertThrows(UnknownWorkflowException.class, () -> inboundMeshService.handleInboundMessage(message, channel, 1234));

        verifyNoInteractions(channel, recepConsumerService, registrationConsumerService);
    }

}
