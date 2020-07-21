package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshMailBoxScheduler;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.uat.CustomArgumentsProvider;
import uk.nhs.digital.nhsconnect.nhais.uat.TestData;
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
    private MeshMailBoxScheduler meshMailBoxScheduler;

    private boolean schedulerConfigured;

    @BeforeEach
    void setUp() {
        System.setProperty("NHAIS_SCHEDULER_ENABLED", "true"); //enable scheduling
        if (!schedulerConfigured) { // do configuration only once per test run
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
        clearMeshMailbox();
        System.setProperty("NHAIS_SCHEDULER_ENABLED", "false");
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(CustomArgumentsProvider.Inbound.class)
    void testTranslatingFromEdifactToFhir(String category, TestData testData) throws JMSException {
        var recipient = new EdifactParser().parse(testData.getEdifact())
            .getInterchangeHeader().getRecipient();

        // send EDIFACT to MESH mailbox
        meshClient.sendEdifactMessage(OutboundMeshMessage.create(
            recipient, WorkflowId.REGISTRATION, testData.getEdifact(), null, null));

        var expectedTransactionType = category.split("/")[0];

        // fetch FHIR from "gp inbound queue"
        var gpSystemInboundQueueMessage = getGpSystemInboundQueueMessage();

        assertThat(gpSystemInboundQueueMessage).isNotNull();

        // assert transaction type in JMS header is correct
        assertMessageHeaders(gpSystemInboundQueueMessage, expectedTransactionType);
        // assert output body is correct
        assertMessageBody(gpSystemInboundQueueMessage, testData.getJson());
    }

    private void assertMessageBody(Message gpSystemInboundQueueMessage, String expectedBody) throws JMSException {
        var body = parseTextMessage(gpSystemInboundQueueMessage);
        assertThat(body).isEqualTo(expectedBody);
    }

    private void assertMessageHeaders(Message gpSystemInboundQueueMessage, String expectedTransactionType) throws JMSException {
        String transactionType = gpSystemInboundQueueMessage.getStringProperty(JmsHeaders.TRANSACTION_TYPE);
        assertThat(transactionType).isEqualTo(expectedTransactionType);
    }
}
