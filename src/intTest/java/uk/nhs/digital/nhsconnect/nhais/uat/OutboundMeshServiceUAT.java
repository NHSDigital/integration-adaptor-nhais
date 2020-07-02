package uk.nhs.digital.nhsconnect.nhais.uat;

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
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(IntegrationTestsExtension.class)
@DirtiesContext
public class OutboundMeshServiceUAT extends MeshServiceBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeshClient meshClient;

    @Autowired
    private MeshConfig meshConfig;

    @MockBean
    private TimestampService timestampService;

    @BeforeEach
    void setUp() {
        when(timestampService.getCurrentTimestamp()).thenReturn(ZonedDateTime
            .of(2020, 6, 10, 14, 38, 10, 0, TimestampService.UKZone)
            .toInstant());
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(CustomArgumentsProvider.Outbound.class)
    void testTranslatingFromFhirToEdifact(String category, TestData testData) throws Exception {
        var transactionType = category.split("/")[0];

        // send EDIFACT to API
        sendToApi(testData.getFhir(), transactionType);

        // fetch EDIFACT message from MESH
        List<String> msgs = meshClient.getInboxMessageIds();

        // assert output EDIFACT is correct
        assertMessageBody(meshClient.getEdifactMessage(msgs.get(0)), testData.getEdifact());

        // acknowledge message will remove it from MESH
        await().atMost(10, SECONDS)
            .untilAsserted(() -> meshClient.acknowledgeMessage(msgs.get(0)));
    }

    private void sendToApi(String fhirInput, String transactionType) throws Exception {
        mockMvc.perform(post("/fhir/Patient/$nhais." + transactionType).contentType("application/json").content(fhirInput))
            .andExpect(status().isAccepted());
    }

    private void assertMessageBody(MeshMessage meshMessage, String expectedEdifact) {
        assertThat(meshMessage.getContent()).isEqualTo(expectedEdifact);
    }
}
