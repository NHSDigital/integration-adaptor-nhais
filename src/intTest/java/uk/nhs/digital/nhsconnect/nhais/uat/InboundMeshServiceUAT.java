package uk.nhs.digital.nhsconnect.nhais.uat;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.jms.MeshServiceBaseTest;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshMailBoxScheduler;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith(IntegrationTestsExtension.class)
@DirtiesContext
public class InboundMeshServiceUAT extends MeshServiceBaseTest {

    @Autowired
    private FhirParser fhirParser;

    @Autowired
    private MeshClient meshClient;

    @Autowired
    private MeshConfig meshConfig;

    @Autowired
    private MeshMailBoxScheduler meshMailBoxScheduler;

    private boolean schedulerConfigured;

    @BeforeEach
    void setUp() {
        System.setProperty("NHAIS_SCHEDULER_ENABLED", "true"); //enable scheduling
        if(!schedulerConfigured) { // do configuration only once per test run
            meshMailBoxScheduler.hasTimePassed(0); //First run creates collection in MongoDb
            await().atMost(10, SECONDS)
                    .pollDelay(500, TimeUnit.MILLISECONDS)
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .untilAsserted(() -> assertThat(meshMailBoxScheduler.hasTimePassed(0)).isTrue()); //wait till it's done
            schedulerConfigured = true;
        }
    }

    @AfterEach
    void tearDown() {
        System.setProperty("NHAIS_SCHEDULER_ENABLED", "false");
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(CustomArgumentsProvider.Inbound.class)
    void testTranslatingFromEdifactToFhir(String category, TestData testData) throws JMSException {
        // send EDIFACT to MESH mailbox
        meshClient.sendEdifactMessage(testData.getEdifact(), meshConfig.getMailboxId());

        var expectedTransactionType = category.split("/")[0];

        // fetch FHIR from "gp inbound queue"
        var gpSystemInboundQueueMessage = getGpSystemInboundQueueMessage();

        // assert transaction type in JMS header is correct
        assertMessageHeaders(gpSystemInboundQueueMessage, expectedTransactionType);
        // assert output FHIR is correct
        assertMessageBody(gpSystemInboundQueueMessage, testData.getFhir());
    }

    private void assertMessageBody(Message gpSystemInboundQueueMessage, String expectedFhir) throws JMSException {
        var resource = parseGpInboundQueueMessage(gpSystemInboundQueueMessage);
        assertThat(resource).isExactlyInstanceOf(Parameters.class);

        String fhir = fhirParser.encodeToString(resource);

        assertThat(fhir).isEqualTo(expectedFhir);
    }

    private void assertMessageHeaders(Message gpSystemInboundQueueMessage, String expectedTransactionType) throws JMSException {
        String transactionType = gpSystemInboundQueueMessage.getStringProperty(JmsHeaders.TRANSACTION_TYPE);
        assertThat(transactionType).isEqualTo(expectedTransactionType);
    }
}
