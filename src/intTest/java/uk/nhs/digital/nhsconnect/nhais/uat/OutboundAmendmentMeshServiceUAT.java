package uk.nhs.digital.nhsconnect.nhais.uat;

import org.awaitility.Durations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.jms.MeshServiceBaseTest;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(IntegrationTestsExtension.class)
@DirtiesContext
public class OutboundAmendmentMeshServiceUAT extends MeshServiceBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimestampService timestampService;

    @BeforeEach
    void setUp() {
        when(timestampService.getCurrentTimestamp()).thenReturn(ZonedDateTime
            .of(2020, 6, 10, 14, 38, 10, 0, TimestampService.UKZone)
            .toInstant());
    }

    @AfterEach
    void tearDown() {
        clearMeshMailbox();
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(CustomArgumentsProvider.OutboundAmendment.class)
    void testTranslatingFromJsonPatchToEdifact(String category, TestData testData) throws Exception {

        // send EDIFACT to API
        sendToApi(testData.getJson());

        // fetch EDIFACT message from MESH
        await().atMost(10, TimeUnit.SECONDS)
            .pollDelay(Durations.ONE_SECOND)
            .pollInterval(Durations.TWO_HUNDRED_MILLISECONDS)
            .until(this::isNewMessageAvailable);

        List<String> messageIds = meshClient.getInboxMessageIds();

        // assert output EDIFACT is correct
        assertMessageBody(meshClient.getEdifactMessage(messageIds.get(0)), testData.getEdifact());
    }

    private void sendToApi(String jsonPatchInput) throws Exception {
        //have to use 9999999999 NHS number in all tests
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(jsonPatchInput))
            .andExpect(status().isAccepted());
    }

    private void assertMessageBody(InboundMeshMessage meshMessage, String expectedEdifact) {
        assertThat(meshMessage.getContent()).isEqualTo(expectedEdifact);
    }

    private Boolean isNewMessageAvailable() {
        return meshClient.getInboxMessageIds().size() > 0;
    }
}
