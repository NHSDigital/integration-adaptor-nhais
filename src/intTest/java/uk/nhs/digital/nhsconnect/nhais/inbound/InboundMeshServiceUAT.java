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
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.uat.common.InboundArgumentsProvider;
import uk.nhs.digital.nhsconnect.nhais.uat.common.TestData;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith(IntegrationTestsExtension.class)
@DirtiesContext
public class InboundMeshServiceUAT extends MeshServiceBaseTest {
    @Autowired
    private FhirParser fhirParser;

    @Autowired
    private EdifactParser edifactParser;

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
    @ArgumentsSource(InboundArgumentsProvider.class)
    void testTranslatingFromEdifactToFhir(String category, TestData testData) throws Exception {
        var recipient = new EdifactParser().parse(testData.getEdifact())
            .getInterchangeHeader().getRecipient();

        // send EDIFACT to MESH mailbox
        meshClient.sendEdifactMessage(OutboundMeshMessage.create(
            recipient, WorkflowId.REGISTRATION, testData.getEdifact(), null, null));

        var expectedTransactionType = category.split("/")[0];

        // fetch FHIR from "gp inbound queue"
        var gpSystemInboundQueueMessage = getGpSystemInboundQueueMessage();
        System.setProperty("NHAIS_SCHEDULER_ENABLED", "false");

        if (category.equals("close_quarter_notification/close-quarter-notification")) {
            verifyThatCloseQuarterNotificationIsNotPresentOnGpSystemInboundQueue(gpSystemInboundQueueMessage);
        } else if (category.equals("close_quarter_notification/close-quarter-notification-other-transactions")) {
            verifyThatNonCloseQuarterNotificationMessageIsTranslated(testData, gpSystemInboundQueueMessage);
        } else {
            assertThat(gpSystemInboundQueueMessage).isNotNull();
            // assert transaction type in JMS header is correct
            assertMessageHeaders(gpSystemInboundQueueMessage, expectedTransactionType);
            // assert output body is correct
            assertMessageBody(gpSystemInboundQueueMessage, testData.getJson());
        }
        assertOutboundRecepMessage(testData.getRecep());
    }

    private void verifyThatCloseQuarterNotificationIsNotPresentOnGpSystemInboundQueue(Message gpSystemInboundQueueMessage) {
        assertThat(gpSystemInboundQueueMessage).isNull();
    }

    private void verifyThatNonCloseQuarterNotificationMessageIsTranslated(TestData testData, Message gpSystemInboundQueueMessage) throws JMSException {
        assertThat(gpSystemInboundQueueMessage).isNotNull();
        // assert transaction type in JMS header is correct
        assertMessageHeaders(gpSystemInboundQueueMessage, "fp69_prior_notification");
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

    private void assertOutboundRecepMessage(String recep) {
        List<String> messageIds = waitFor(() -> {
            List<String> inboxMessageIds = meshClient.getInboxMessageIds();
            return inboxMessageIds.isEmpty() ? null : inboxMessageIds;
        } );
        var meshMessage = meshClient.getEdifactMessage(messageIds.get(0));

        Interchange expectedRecep = edifactParser.parse(recep);
        Interchange actualRecep = edifactParser.parse(meshMessage.getContent());

        assertThat(meshMessage.getWorkflowId()).isEqualTo(WorkflowId.RECEP);
        assertThat(actualRecep.getInterchangeHeader().getRecipient()).isEqualTo(expectedRecep.getInterchangeHeader().getRecipient());
        assertThat(actualRecep.getInterchangeHeader().getSender()).isEqualTo(expectedRecep.getInterchangeHeader().getSender());
        assertThat(actualRecep.getInterchangeHeader().getSequenceNumber()).isEqualTo(expectedRecep.getInterchangeHeader().getSequenceNumber());
        assertThat(filterTimestampedSegments(actualRecep)).containsExactlyElementsOf(filterTimestampedSegments(expectedRecep));
        assertThat(actualRecep.getInterchangeTrailer().getNumberOfMessages()).isEqualTo(expectedRecep.getInterchangeTrailer().getNumberOfMessages());
        assertThat(actualRecep.getInterchangeTrailer().getSequenceNumber()).isEqualTo(expectedRecep.getInterchangeTrailer().getSequenceNumber());
    }

    private List<String> filterTimestampedSegments(Interchange recep) {
        List<String> edifactSegments = recep.getMessages().get(0).getEdifactSegments();
        assertThat(edifactSegments).anySatisfy(segment -> assertThat(segment.startsWith("BGM+")));
        assertThat(edifactSegments).anySatisfy(segment -> assertThat(segment.startsWith("DTM+815")));

        return edifactSegments.stream()
            .filter(segment -> !segment.startsWith("BGM+"))
            .filter(segment -> !segment.startsWith("DTM+815"))
            .collect(Collectors.toList());
    }
}
