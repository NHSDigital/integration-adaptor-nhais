package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationConsumerServiceTest {

    private static final String OPERATION_ID = "bd0327c35d94d2972b4e0c99e355a8bb5ea2453eb27777d9e1985af38c9c2cf2";

    @Mock
    EdifactParser edifactParser;

    @Mock
    EdifactToFhirService edifactToFhirService;

    @Mock
    InboundGpSystemService inboundGpSystemService;

    @Mock
    InboundStateRepository inboundStateRepository;

    @InjectMocks
    RegistrationConsumerService registrationConsumerService;

    @Test
    public void registrationMessage_publishedToSupplierQueue() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshMessage.setContent("CONTENT");
        when(edifactParser.parse("CONTENT")).thenReturn(InboundStateTest.INTERCHANGE);
        Parameters parameters = new Parameters();
        when(edifactToFhirService.convertToFhir(any(Interchange.class))).thenReturn(parameters);

        registrationConsumerService.handleRegistration(meshMessage);

        ArgumentCaptor<InboundState> inboundStateArgumentCaptor = ArgumentCaptor.forClass(InboundState.class);
        verify(inboundStateRepository).save(inboundStateArgumentCaptor.capture());
        InboundState savedInboundState = inboundStateArgumentCaptor.getValue();
        assertEquals(savedInboundState, InboundStateTest.INBOUND_STATE);

        verify(inboundGpSystemService).publishToSupplierQueue(parameters, OPERATION_ID);
    }

    @Test
    public void whenDuplicateMessageErrorOnInboundStateSave_thenNotPublishedToSupplierQueue() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshMessage.setContent("CONTENT");
        when(edifactParser.parse("CONTENT")).thenReturn(InboundStateTest.INTERCHANGE);
        when(inboundStateRepository.save(InboundStateTest.INBOUND_STATE)).thenThrow(DuplicateKeyException.class);

        registrationConsumerService.handleRegistration(meshMessage);

        verifyNoInteractions(edifactToFhirService);
        verifyNoInteractions(inboundGpSystemService);
    }
}
