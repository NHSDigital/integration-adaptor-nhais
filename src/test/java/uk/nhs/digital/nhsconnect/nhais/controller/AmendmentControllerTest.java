package uk.nhs.digital.nhsconnect.nhais.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.service.JsonPatchToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.service.OutboundQueueService;

import java.nio.file.Files;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("component")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AmendmentController.class)
public class AmendmentControllerTest {

    private static final String OPERATION_ID = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JsonPatchToEdifactService jsonPatchToEdifactService;

    @MockBean
    private OutboundQueueService outboundQueueService;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private FhirParser fhirParser;

    @Value("classpath:/amendment/amendment.json")
    private Resource paramsPayload;

    @Test
    void whenValidInput_thenReturns202() throws Exception {
        String requestBody = new String(Files.readAllBytes(paramsPayload.getFile().toPath()));

        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setContent("EDI");
        meshMessage.setOperationId(OPERATION_ID);
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);

        when(jsonPatchToEdifactService.convertToEdifact(any(AmendmentBody.class))).thenReturn(meshMessage);

        mockMvc.perform(patch("/fhir/Patient/1234567890")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(header().string("OperationId", OPERATION_ID));

        verify(outboundQueueService).publish(any(MeshMessage.class));
    }
}
