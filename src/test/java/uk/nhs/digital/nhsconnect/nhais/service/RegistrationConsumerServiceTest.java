package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationConsumerServiceTest {

    @Mock
    EdifactParser edifactParser;

    @Mock
    EdifactToFhirService edifactToFhirService;

    @Mock
    InboundGpSystemService inboundGpSystemService;

    @InjectMocks
    RegistrationConsumerService registrationConsumerService;

    @Test
    public void registrationMessage_publishedToSupplierQueue() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        meshMessage.setContent("CONTENT");
        Interchange interchange = new Interchange();
        when(edifactParser.parse("CONTENT")).thenReturn(interchange);
        Parameters parameters = new Parameters();
        when(edifactToFhirService.convertToFhir(any(Interchange.class))).thenReturn(parameters);

        registrationConsumerService.handleRegistration(meshMessage);

        verify(inboundGpSystemService).publishToSupplierQueue(parameters);
    }

}
