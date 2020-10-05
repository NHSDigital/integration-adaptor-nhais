package uk.nhs.digital.nhsconnect.nhais.outbound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirController;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;

import java.nio.file.Files;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("component")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FhirController.class)
public class FhirControllerTest {

    private static final String OPERATION_ID = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:/patient/parameters.json")
    private Resource paramsPayload;

    @MockBean
    private OutboundQueueService outboundQueueService;

    @MockBean
    private FhirToEdifactService fhirToEdifactService;

    @MockBean
    private FhirParser fhirParser;

    @SpyBean
    private ConversationIdService conversationIdService;

    @Test
    void whenValidAcceptanceInput_thenReturns202() throws Exception {
        String requestBody = new String(Files.readAllBytes(paramsPayload.getFile().toPath()));
        MeshMessage meshMessage = getMeshMessage();
        meshMessage.setContent("EDI");
        meshMessage.setOperationId(OPERATION_ID);
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);

        when(fhirParser.parseParameters(requestBody)).thenReturn(new Parameters());
        when(fhirToEdifactService.convertToEdifact(any(Parameters.class), any())).thenReturn(meshMessage);

        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("application/json")
            .content(requestBody))
            .andExpect(status().isAccepted())
            .andExpect(header().string("OperationId", OPERATION_ID));

        verify(outboundQueueService).publish(any(MeshMessage.class));
    }

    @Test
    void whenValidRemovalInput_thenReturns202() throws Exception {
        String requestBody = new String(Files.readAllBytes(paramsPayload.getFile().toPath()));
        MeshMessage meshMessage = getMeshMessage();

        when(fhirParser.parseParameters(requestBody)).thenReturn(new Parameters());
        when(fhirToEdifactService.convertToEdifact(any(Parameters.class), any())).thenReturn(meshMessage);

        mockMvc.perform(post("/fhir/Patient/$nhais.removal")
            .contentType("application/json")
            .content(requestBody))
            .andExpect(status().isAccepted())
            .andExpect(header().string("OperationId", OPERATION_ID));

        verify(outboundQueueService).publish(meshMessage);
    }

    @Test
    void whenValidDeductionInput_thenReturns202() throws Exception {
        String requestBody = new String(Files.readAllBytes(paramsPayload.getFile().toPath()));
        MeshMessage meshMessage = getMeshMessage();

        when(fhirParser.parseParameters(requestBody)).thenReturn(new Parameters());
        when(fhirToEdifactService.convertToEdifact(any(Parameters.class), any())).thenReturn(meshMessage);

        mockMvc.perform(post("/fhir/Patient/$nhais.removal")
            .contentType("application/json")
            .content(requestBody))
            .andExpect(status().isAccepted())
            .andExpect(header().string("OperationId", OPERATION_ID));

        verify(outboundQueueService).publish(meshMessage);
    }

    @NotNull
    private MeshMessage getMeshMessage() {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setContent("EDI");
        meshMessage.setOperationId(OPERATION_ID);
        meshMessage.setWorkflowId(WorkflowId.REGISTRATION);
        return meshMessage;
    }

    @Test
    void whenInvalidInput_thenReturns400() throws Exception {
        String requestBody = "{}";
        when(fhirParser.parseParameters(requestBody)).thenThrow(new FhirValidationException("the message"));
        String expectedResponse = "{\"expected\":\"response\"}";
        when(fhirParser.encodeToString(any(OperationOutcome.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("application/json")
            .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(expectedResponse));
    }

    @Test
    void whenUnhandledException_thenReturns500() throws Exception {
        String requestBody = "{}";
        when(fhirParser.parseParameters(requestBody)).thenThrow(new RuntimeException("the message"));
        String expectedResponse = "{\"expected\":\"response\"}";
        when(fhirParser.encodeToString(any(OperationOutcome.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("application/json")
            .content(requestBody))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(expectedResponse));
    }

    @Test
    void whenInvalidMediaType_thenReturn415() throws Exception {
        String expectedResponse = "{\"expected\":\"response\"}";
        when(fhirParser.encodeToString(any(OperationOutcome.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("text/plain")
            .content("qwe"))
            .andExpect(status().is(415))
            .andExpect(content().json(expectedResponse));

        var operationOutcomeArgumentCaptor = ArgumentCaptor.forClass(OperationOutcome.class);
        verify(fhirParser).encodeToString(operationOutcomeArgumentCaptor.capture());
        var operationOutcome = operationOutcomeArgumentCaptor.getValue();

        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).isEqualTo("Content type 'text/plain' not supported");
    }
}
