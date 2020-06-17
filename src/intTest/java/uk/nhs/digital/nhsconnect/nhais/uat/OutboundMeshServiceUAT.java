package uk.nhs.digital.nhsconnect.nhais.uat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.jms.MeshServiceBaseTest;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import javax.jms.JMSException;
import javax.jms.Message;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class OutboundMeshServiceUAT extends MeshServiceBaseTest {
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

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(CustomArgumentsProvider.Outbound.class)
    void test(String category, TestData testData) throws Exception {
        var transactionType = category.split("/")[0];

        // send EDIFACT to API
        sendToApi(testData.getFhir(), transactionType);

        // fetch EDIFACT from "outbound queue"
        var gpOutboundQueueMessage = getOutboundQueueMessage();

        // assert output EDIFACT is correct
        assertMessageBody(gpOutboundQueueMessage, testData.getEdifact());
    }

    private void sendToApi(String fhirInput, String transactionType) throws Exception {
//        mockMvc.perform(post("/fhir/Patient/" + transactionType).contentType("application/json").content(fhirInput))
        mockMvc.perform(post("/fhir/Patient/1234").contentType("application/json").content(fhirInput))
            .andExpect(status().isAccepted());
    }

    private void assertMessageBody(Message gpSystemInboundQueueMessage, String expectedEdifact) throws JMSException {
        var meshMessage = parseOutboundQueueMessage(gpSystemInboundQueueMessage);

        assertThat(meshMessage.getContent()).isEqualTo(expectedEdifact);
    }
}
