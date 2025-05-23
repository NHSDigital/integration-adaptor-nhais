package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.assertj.core.api.SoftAssertions;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.IntegrationBaseTest;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests the processing of a REGISTRATION interchange containing multiple messages and transactions by publishing it
 * onto the inbound MESH message queue. This bypasses the MESH polling loop / MESH Client / MESH API.
 */
@DirtiesContext
public class InboundMeshQueueMultiTransactionTest extends IntegrationBaseTest {

    private static final String SENDER = "XX11";
    private static final String RECIPIENT = "TES5";
    private static final long SIS = 3;
    private static final long SMS_1 = 6;
    private static final long SMS_2 = 7;
    private static final long SMS_3 = 8;
    private static final long SMS_4 = 9;
    private static final long TN_1 = 101;
    private static final long TN_2 = 100;
    private static final long TN_3 = 102;
    private static final long TN_4 = 12;
    private static final long TN_5 = 13;
    private static final long TN_6 = 14;
    private static final ReferenceTransactionType.Inbound MESSAGE_1_TRANSACTION_TYPE = ReferenceTransactionType.Inbound.AMENDMENT;
    private static final ReferenceTransactionType.Inbound MESSAGE_2_TRANSACTION_TYPE = ReferenceTransactionType.Inbound.DEDUCTION;
    private static final ReferenceTransactionType.Inbound MESSAGE_3_TRANSACTION_TYPE = ReferenceTransactionType.Inbound.REJECTION;
    private static final ReferenceTransactionType.Inbound MESSAGE_4_TRANSACTION_TYPE = ReferenceTransactionType.Inbound.APPROVAL;
    private static final String TRANSACTION_1_OPERATION_ID = OperationId.buildOperationId(SENDER, TN_1);
    private static final String TRANSACTION_2_OPERATION_ID = OperationId.buildOperationId(SENDER, TN_2);
    private static final String TRANSACTION_3_OPERATION_ID = OperationId.buildOperationId(SENDER, TN_3);
    private static final String TRANSACTION_4_OPERATION_ID = OperationId.buildOperationId(RECIPIENT, TN_4);
    private static final String TRANSACTION_5_OPERATION_ID = OperationId.buildOperationId(RECIPIENT, TN_5);
    private static final String TRANSACTION_6_OPERATION_ID = OperationId.buildOperationId(RECIPIENT, TN_6);
    private static final int TN1_INDEX = 0;
    private static final int TN2_INDEX = 1;
    private static final int TN3_INDEX = 2;
    private static final int TN4_INDEX = 3;
    private static final int TN5_INDEX = 4;
    private static final int TN6_INDEX = 5;
    private static final int INBOUND_STATE_EXPECTED_SIZE = 6;
    private static final Instant INTERCHANGE_TRANSLATION_TIMESTAMP = ZonedDateTime
        .of(LocalDateTime.parse("2020-01-25T12:35:00"), TimestampService.UK_ZONE)
        .toInstant();
    private static final Instant GENERATED_TIMESTAMP = ZonedDateTime
        .of(LocalDateTime.parse("2020-06-10T14:38:00"), TimestampService.UK_ZONE)
        .toInstant();
    private static final String ISO_GENERATED_TIMESTAMP = new TimestampService()
        .formatInISO(GENERATED_TIMESTAMP);

    @MockBean
    private TimestampService timestampService;

    @Value("classpath:edifact/multi_transaction.1.dat")
    private Resource interchange;
    @Value("classpath:edifact/multi_transaction.1.fhir.TN-1.json")
    private Resource fhirTN1;
    @Value("classpath:edifact/multi_transaction.1.fhir.TN-2.json")
    private Resource fhirTN2;
    @Value("classpath:edifact/multi_transaction.1.fhir.TN-3.json")
    private Resource fhirTN3;
    @Value("classpath:edifact/multi_transaction.1.fhir.TN-4.json")
    private Resource fhirTN4;
    @Value("classpath:edifact/multi_transaction.1.fhir.TN-5.json")
    private Resource fhirTN5;
    @Value("classpath:edifact/multi_transaction.1.fhir.TN-6.json")
    private Resource fhirTN6;
    @Value("classpath:edifact/multi_transaction.1.recep.dat")
    private Resource recep;

    private String previousConversationId;

    @BeforeEach
    void setUp() {
        when(timestampService.getCurrentTimestamp()).thenReturn(GENERATED_TIMESTAMP);
        when(timestampService.formatInISO(GENERATED_TIMESTAMP)).thenReturn(ISO_GENERATED_TIMESTAMP);
        clearGpSystemInboundQueue();
        clearMeshMailboxes();
    }

    @Test
    void When_MeshInboundQueueRegistrationMessageIsReceived_Expect_MessageIsHandled(SoftAssertions softly)
        throws IOException, JMSException, JSONException {

        var meshMessage = new MeshMessage()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setContent(new String(Files.readAllBytes(interchange.getFile().toPath())));

        sendToMeshInboundQueue(meshMessage);

        var inboundStates = waitFor(this::getAllInboundStates);

        assertGpSystemInboundQueueMessages(softly);
        assertInboundStates(softly, inboundStates);
        assertOutboundRecepMessage(softly);
        assertOutboundState(softly);
    }

    private List<InboundState> getAllInboundStates() {
        var inboundState1 = findInboundState(SMS_1, TN_1);
        var inboundState2 = findInboundState(SMS_2, TN_2);
        var inboundState3 = findInboundState(SMS_2, TN_3);
        var inboundState4 = findInboundState(SMS_3, TN_4);
        var inboundState5 = findInboundState(SMS_4, TN_5);
        var inboundState6 = findInboundState(SMS_4, TN_6);

        var inboundStates = Stream.of(inboundState1, inboundState2, inboundState3, inboundState4, inboundState5, inboundState6)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

        if (inboundStates.size() == INBOUND_STATE_EXPECTED_SIZE) {
            return inboundStates;
        }
        return null;
    }

    private Optional<InboundState> findInboundState(long sms, long tn) {
        return super.getInboundStateRepository().findBy(WorkflowId.REGISTRATION, SENDER, RECIPIENT, SIS, sms, tn);
    }

    private void assertOutboundRecepMessage(SoftAssertions softly) throws IOException {

        var meshMessage = waitForMeshMessage(super.getNhaisMeshClient());

        softly.assertThat(meshMessage.getContent()).isEqualTo(new String(Files.readAllBytes(recep.getFile().toPath())));
        softly.assertThat(meshMessage.getWorkflowId()).isEqualTo(WorkflowId.RECEP);

        // timestamp is not set in mesh message payload for outbound RECEP
        softly.assertThat(meshMessage.getMessageSentTimestamp()).isNull();
    }

    private void assertGpSystemInboundQueueMessages(SoftAssertions softly) throws JMSException, IOException, JSONException {
        var gpSystemInboundQueueMessages = IntStream.range(0, INBOUND_STATE_EXPECTED_SIZE)
            .mapToObj(x -> getGpSystemInboundQueueMessage())
            .toList();

        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(TN1_INDEX), MESSAGE_1_TRANSACTION_TYPE, TRANSACTION_1_OPERATION_ID, fhirTN1);
        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(TN2_INDEX), MESSAGE_2_TRANSACTION_TYPE, TRANSACTION_2_OPERATION_ID, fhirTN2);
        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(TN3_INDEX), MESSAGE_2_TRANSACTION_TYPE, TRANSACTION_3_OPERATION_ID, fhirTN3);
        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(TN4_INDEX), MESSAGE_3_TRANSACTION_TYPE, TRANSACTION_4_OPERATION_ID, fhirTN4);
        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(TN5_INDEX), MESSAGE_4_TRANSACTION_TYPE, TRANSACTION_5_OPERATION_ID, fhirTN5);
        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(TN6_INDEX), MESSAGE_4_TRANSACTION_TYPE, TRANSACTION_6_OPERATION_ID, fhirTN6);
    }

    private void assertGpSystemInboundQueueMessages(
        SoftAssertions softly,
        Message message,
        ReferenceTransactionType.TransactionType expectedTransactionType,
        String expectedOperationId,
        Resource expectedFhir) throws JMSException, IOException, JSONException {

        // all transactions come from the same interchange and use the same conversation id
        String conversationId = message.getStringProperty("ConversationId");
        if (previousConversationId == null) {
            previousConversationId = conversationId;
        }
        softly.assertThat(conversationId).isEqualTo(previousConversationId);

        softly.assertThat(message.getStringProperty("OperationId"))
            .isEqualTo(expectedOperationId);
        softly.assertThat(message.getStringProperty("TransactionType"))
            .isEqualTo(expectedTransactionType.name().toLowerCase());

        JSONAssert.assertEquals(
            parseTextMessage(message),
            new String(Files.readAllBytes(expectedFhir.getFile().toPath())),
            JSONCompareMode.STRICT
        );
    }

    private void assertInboundStates(SoftAssertions softly, List<InboundState> inboundStates) {
        softly.assertThat(inboundStates).hasSize(INBOUND_STATE_EXPECTED_SIZE);

        assertInboundState(
            softly, inboundStates.get(TN1_INDEX), TRANSACTION_1_OPERATION_ID, SMS_1, TN_1, MESSAGE_1_TRANSACTION_TYPE);
        assertInboundState(
            softly, inboundStates.get(TN2_INDEX), TRANSACTION_2_OPERATION_ID, SMS_2, TN_2, MESSAGE_2_TRANSACTION_TYPE);
        assertInboundState(
            softly, inboundStates.get(TN3_INDEX), TRANSACTION_3_OPERATION_ID, SMS_2, TN_3, MESSAGE_2_TRANSACTION_TYPE);
        assertInboundState(
            softly, inboundStates.get(TN4_INDEX), TRANSACTION_4_OPERATION_ID, SMS_3, TN_4, MESSAGE_3_TRANSACTION_TYPE);
        assertInboundState(
            softly, inboundStates.get(TN5_INDEX), TRANSACTION_5_OPERATION_ID, SMS_4, TN_5, MESSAGE_4_TRANSACTION_TYPE);
        assertInboundState(
            softly, inboundStates.get(TN6_INDEX), TRANSACTION_6_OPERATION_ID, SMS_4, TN_6, MESSAGE_4_TRANSACTION_TYPE);
    }

    private void assertInboundState(
        SoftAssertions softly,
        InboundState inboundStates,
        String expectedOperationId,
        Long expectedSMS,
        Long expectedTN,
        ReferenceTransactionType.Inbound expectedTransactionType) {

        // must assert inbound state after the message so previousConversationId is populated
        var expectedInboundState = new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setOperationId(expectedOperationId)
            .setInterchangeSequence(SIS)
            .setMessageSequence(expectedSMS)
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setTransactionType(expectedTransactionType)
            .setTransactionNumber(expectedTN)
            .setTranslationTimestamp(INTERCHANGE_TRANSLATION_TIMESTAMP)
            .setProcessedTimestamp(GENERATED_TIMESTAMP)
            .setConversationId(previousConversationId);

        softly.assertThat(inboundStates).isEqualToIgnoringGivenFields(expectedInboundState, "id");
    }

    private void assertOutboundState(SoftAssertions softly) {
        waitForCondition(() -> super.getOutboundStateRepository().findAll().iterator().hasNext());
        Iterable<OutboundState> outboundStates = super.getOutboundStateRepository().findAll();

        assertThat(outboundStates).hasSize(1);
        var outboundState = outboundStates.iterator().next();
        var expected = new OutboundState()
            .setInterchangeSequence(1L)
            .setMessageSequence(1L)
            .setRecipient(SENDER)
            .setSender(RECIPIENT)
            .setWorkflowId(WorkflowId.RECEP)
            .setTranslationTimestamp(GENERATED_TIMESTAMP);
        softly.assertThat(outboundState).isEqualToIgnoringGivenFields(expected, "id");
    }
}
