package uk.nhs.digital.nhsconnect.nhais.outbound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.nhs.digital.nhsconnect.nhais.IntegrationBaseTest;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.uat.common.OutboundArgumentsProvider;
import uk.nhs.digital.nhsconnect.nhais.uat.common.TestData;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Reads test data from /outbound_uat_data. The FHIR .json files are sent to the adaptor's API. The test waits for the
 * transaction to be processed and compares the EDIFACT sent to the MESH mailbox to the .dat file having the name name
 * as the .json
 */
@AutoConfigureMockMvc
@ExtendWith(IntegrationTestsExtension.class)
@DirtiesContext
public class OutboundUserAcceptanceTest extends IntegrationBaseTest {
    private static final String RECIPIENT = "XX11";
    private static final String SENDER = "TES5";
    private static final Instant GENERATED_TIMESTAMP = ZonedDateTime
        .of(2020, 6, 10, 14, 38, 10, 0, TimestampService.UKZone)
        .toInstant();
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private TimestampService timestampService;

    private String correlationId;
    private String operationId;

    @BeforeEach
    void setUp() {
        when(timestampService.getCurrentTimestamp()).thenReturn(GENERATED_TIMESTAMP);
        clearMeshMailboxes();
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(OutboundArgumentsProvider.class)
    void testTranslatingFromFhirToEdifact(String category, TestData testData) throws Exception {
        var transactionType = category.split("/")[0];

        // send EDIFACT to API
        sendToApi(testData.getJson(), transactionType);

        // fetch EDIFACT message from MESH
        var meshMessage = waitForMeshMessage(nhaisMeshClient);

        // assert output EDIFACT is correct
        assertMessageBody(meshMessage, testData.getEdifact());
        assertOutboundState(transactionType);
    }

    private void sendToApi(String jsonInput, String transactionType) throws Exception {
        MvcResult result;
        if (transactionType.equals("amendment")) {
            //have to use 9999999999 NHS number in all tests
            result = mockMvc.perform(patch("/fhir/Patient/9999999999")
                .contentType("application/json")
                .content(jsonInput))
                .andExpect(status().isAccepted())
                .andReturn();
        } else {
            result = mockMvc.perform(post("/fhir/Patient/$nhais." + transactionType)
                .contentType("application/json")
                .content(jsonInput))
                .andExpect(status().isAccepted())
                .andReturn();
        }
        this.operationId = result.getResponse().getHeader("OperationId");
        this.correlationId = result.getResponse().getHeader(CorrelationIdFilter.KEY);
    }

    private void assertMessageBody(InboundMeshMessage meshMessage, String expectedEdifact) {
        assertThat(meshMessage.getContent()).isEqualTo(expectedEdifact);
    }

    private void assertOutboundState(String transactionTypeString) {
        Iterable<OutboundState> outboundStates = waitFor(() -> {
            var result = outboundStateRepository.findAll();
            return result.iterator().hasNext() ? result : null;
        });

        assertThat(outboundStates).hasSize(1);
        var outboundState = outboundStates.iterator().next();
        var expected = new OutboundState()
            .setInterchangeSequence(1L)
            .setMessageSequence(1L)
            .setRecipient(RECIPIENT)
            .setSender(SENDER)
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setTranslationTimestamp(GENERATED_TIMESTAMP)
            .setCorrelationId(correlationId)
            .setOperationId(operationId)
            .setTransactionNumber(1L)
            .setTransactionType(ReferenceTransactionType.Outbound.valueOf(transactionTypeString.toUpperCase()));
        assertThat(outboundState).isEqualToIgnoringGivenFields(expected, "id");
    }
}
