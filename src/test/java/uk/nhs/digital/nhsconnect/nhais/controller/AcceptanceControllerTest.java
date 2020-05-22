package uk.nhs.digital.nhsconnect.nhais.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.EdifactToMeshMessageService;
import uk.nhs.digital.nhsconnect.nhais.service.FhirToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.service.OutboundMeshService;

import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AcceptanceController.class)
public class AcceptanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:patient.json")
    private Resource patientPayload;

    @MockBean
    private OutboundMeshService outboundMeshService;

    @MockBean
    private FhirToEdifactService fhirToEdifactService;

    @MockBean
    private EdifactToMeshMessageService edifactToMeshMessageService;

    @Test
    void whenValidInput_thenReturns202() throws Exception {
        String requestBody = new String(Files.readAllBytes(patientPayload.getFile().toPath()));
        TranslatedInterchange translatedInterchange = new TranslatedInterchange();
        translatedInterchange.setEdifact("EDI");
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setInterchange("EDI");
        meshMessage.setWorkflowId("workflowId");
        meshMessage.setOdsCode("odsCode");
        when(fhirToEdifactService.convertToEdifact(any(Patient.class))).thenReturn(translatedInterchange);
        when(edifactToMeshMessageService.toMeshMessage(translatedInterchange)).thenReturn(meshMessage);

        mockMvc.perform(post("/fhir/Patient/12345")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isAccepted());

        verify(outboundMeshService).send(meshMessage);
    }
}
