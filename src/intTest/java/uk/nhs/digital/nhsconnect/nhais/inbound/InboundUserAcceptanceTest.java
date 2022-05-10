package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.IntegrationBaseTest;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reads test data from /inbound_uat_data. The EDIFACT .dat files are sent to the MESH mailbox where the adaptor receives
 * inbound transactions. The test waits for the transaction to be processed and compares the FHIR published to the GP
 * System Inbound Queue to the .json file having the name name as the .dat.
 */
@ExtendWith(IntegrationTestsExtension.class)
@DirtiesContext
public class InboundUserAcceptanceTest extends IntegrationBaseTest {
    @Autowired
    private FhirParser fhirParser;

    @Autowired
    private EdifactParser edifactParser;

    @Autowired
    private MeshMailBoxScheduler meshMailBoxScheduler;

    private String previousConversationId;

    @BeforeEach
    void beforeEach() {
        clearMeshMailboxes();
        clearGpSystemInboundQueue();
        System.setProperty("NHAIS_SCHEDULER_ENABLED", "true"); //enable scheduling
    }

    @AfterEach
    void tearDown() {
        System.setProperty("NHAIS_SCHEDULER_ENABLED", "false");
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @ArgumentsSource(InboundArgumentsProvider.class)
    void testTranslatingFromEdifactToFhir(String category, TestData testData) throws Exception {
        var recipient = new EdifactParser().parse(testData.getEdifact())
            .getInterchangeHeader().getRecipient();

        // Acting as an NHAIS system, send EDIFACT to adaptor's MESH mailbox
        nhaisMeshClient.sendEdifactMessage(OutboundMeshMessage.create(
            recipient, WorkflowId.REGISTRATION, testData.getEdifact(), null, null));

        var expectedTransactionType = category.split("/")[0];

        Message gpSystemInboundQueueMessage = getGpSystemInboundQueueMessageWithCloseQuarterWorkaround(category);

        if (category.equals("close_quarter_notification/close-quarter-notification")) {
            verifyThatCloseQuarterNotificationIsNotPresentOnGpSystemInboundQueue(gpSystemInboundQueueMessage);
        } else if (category.equals("close_quarter_notification/close-quarter-notification-other-transactions")) {
            verifyThatNonCloseQuarterNotificationMessageIsTranslated(testData, gpSystemInboundQueueMessage);
        } else {
            assertThat(gpSystemInboundQueueMessage).isNotNull();
            // assert transaction type in JMS header is correct
            assertMessageHeaders(gpSystemInboundQueueMessage, expectedTransactionType);
            // assert output body is correct
            assertMessageBody(gpSystemInboundQueueMessage.toString().replaceAll("\\s+",""), testData.getJson().replaceAll("\\s+",""));
        }
        assertOutboundRecepMessage(testData.getRecep());
    }

    private Message getGpSystemInboundQueueMessageWithCloseQuarterWorkaround(String category) {
        if (category.equals("close_quarter_notification/close-quarter-notification")) {
            // there should be no inbound gp system message for close quarter
            // fetch without the helper and waitFor since we expect this to be null so only need to try once
            return jmsTemplate.receive(gpSystemInboundQueueName);
        } else {
            // use the helper method that includes a more robust waitFor
            return getGpSystemInboundQueueMessage();
        }
    }

    private void verifyThatCloseQuarterNotificationIsNotPresentOnGpSystemInboundQueue(Message gpSystemInboundQueueMessage) {
        assertThat(gpSystemInboundQueueMessage).isNull();
    }

    private void verifyThatNonCloseQuarterNotificationMessageIsTranslated(TestData testData, Message gpSystemInboundQueueMessage) throws JMSException {
        assertThat(gpSystemInboundQueueMessage).isNotNull();
        // assert transaction type in JMS header is correct
        assertMessageHeaders(gpSystemInboundQueueMessage, "fp69_prior_notification");
        // assert output body is correct
        assertMessageBody(gpSystemInboundQueueMessage.toString().replaceAll("\\s+",""), testData.getJson().replaceAll("\\s+",""));
    }

    private void assertMessageBody(String gpSystemInboundQueueMessage, String expectedBody) throws JMSException {
        assertThat(gpSystemInboundQueueMessage).isEqualTo(expectedBody);
    }

    private void assertMessageHeaders(Message gpSystemInboundQueueMessage, String expectedTransactionType) throws JMSException {
        String transactionType = gpSystemInboundQueueMessage.getStringProperty(JmsHeaders.TRANSACTION_TYPE);
        assertThat(transactionType).isEqualTo(expectedTransactionType);
        String actualConversationId = gpSystemInboundQueueMessage.getStringProperty(JmsHeaders.CONVERSATION_ID);
        assertThat(actualConversationId).matches("[A-F0-9]{32}");
        assertThat(actualConversationId).isNotEqualTo(previousConversationId);
        previousConversationId = actualConversationId;
    }

    private void assertOutboundRecepMessage(String recep) {
        // Acting as an NHAIS system, receive and validate the RECEP returned by the adaptor
        List<String> messageIds = waitFor(() -> {
            List<String> inboxMessageIds = nhaisMeshClient.getInboxMessageIds();
            return inboxMessageIds.isEmpty() ? null : inboxMessageIds;
        } );
        var meshMessage = nhaisMeshClient.getEdifactMessage(messageIds.get(0));

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
