package uk.nhs.digital.nhsconnect.nhais.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.service.InboundMeshService;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@DirtiesContext
public class InboundMeshServiceMultiTransactionTest extends MeshServiceBaseTest {

    private static final String SENDER = "TES5";
    private static final String RECIPIENT = "XX11";
    private static final long SIS = 7;
    private static final long SMS_1 = 8;
    private static final long SMS_2 = 4;
    private static final long TN_1 = 22;
    private static final long TN_2 = 23;
    private static final long TN_3 = 18;
    private static final ReferenceTransactionType.TransactionType MESSAGE_1_TRANSACTION_TYPE = ReferenceTransactionType.TransactionType.APPROVAL;
    private static final ReferenceTransactionType.TransactionType MESSAGE_2_TRANSACTION_TYPE = ReferenceTransactionType.TransactionType.ACCEPTANCE;
    private static final String TRANSACTION_1_OPERATION_ID = OperationId.buildOperationId(RECIPIENT, TN_1);
    private static final String TRANSACTION_2_OPERATION_ID = OperationId.buildOperationId(RECIPIENT, TN_2);
    private static final String TRANSACTION_3_OPERATION_ID = OperationId.buildOperationId(RECIPIENT, TN_3);
    private static final Instant MESSAGE_1_TRANSLATION_TIMESTAMP = ZonedDateTime
        .of(1992, 1, 17, 12, 59, 0, 0, TimestampService.UKZone)
        .toInstant();
    private static final Instant MESSAGE_2_TRANSLATION_TIMESTAMP = ZonedDateTime
        .of(1992, 1, 14, 16, 19, 0, 0, TimestampService.UKZone)
        .toInstant();

    @Value("classpath:edifact/multi_transaction.1.dat")
    private Resource interchange;

    @Value("classpath:edifact/multi_transaction.1.fhir.TN-1.json")
    private Resource fhirTN1;

    @Value("classpath:edifact/multi_transaction.1.fhir.TN-2.json")
    private Resource fhirTN2;

    @Value("classpath:edifact/multi_transaction.1.fhir.TN-3.json")
    private Resource fhirTN3;

    @Value("classpath:edifact/multi_transaction_recep.dat")
    private Resource recep;

    @Test
    @DirtiesContext
    void whenMeshInboundQueueRegistrationMessageIsReceived_thenMessageIsHandled(SoftAssertions softly) throws IOException, JMSException {
        var meshMessage = new MeshMessage()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setContent(new String(Files.readAllBytes(interchange.getFile().toPath())));

        sendToMeshInboundQueue(meshMessage);

        var inboundStates = waitFor(this::getAllInboundStates);

        assertInboundStates(softly, inboundStates);

        assertGpSystemInboundQueueMessages(softly);

//        assertOutboundQueueRecepMessage(softly);
    }

    private List<InboundState> getAllInboundStates() {
        var inboundState1 = inboundStateRepository.findBy(WorkflowId.REGISTRATION, SENDER, RECIPIENT, SIS, SMS_1, TN_1);
        var inboundState2 = inboundStateRepository.findBy(WorkflowId.REGISTRATION, SENDER, RECIPIENT, SIS, SMS_1, TN_2);
        var inboundState3 = inboundStateRepository.findBy(WorkflowId.REGISTRATION, SENDER, RECIPIENT, SIS, SMS_2, TN_3);

        var inboundStates = Stream.of(inboundState1, inboundState2, inboundState3)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        if (inboundStates.size() == 3) {
            return inboundStates;
        }
        return null;
    }

    private void assertOutboundQueueRecepMessage(SoftAssertions softly) throws JMSException, IOException {
        var gpSystemInboundQueueMessage = getOutboundQueueMessage();

        var meshMessage = parseOutboundMessage(gpSystemInboundQueueMessage);

        softly.assertThat(meshMessage.getContent()).isEqualTo(new String(Files.readAllBytes(recep.getFile().toPath())));
        softly.assertThat(meshMessage.getWorkflowId()).isEqualTo(WorkflowId.RECEP);
        softly.assertThat(meshMessage.getMessageSentTimestamp()).isNotNull();
        //TODO: other assertions on recep message
    }

    private void assertGpSystemInboundQueueMessages(SoftAssertions softly) throws JMSException, IOException {
        var gpSystemInboundQueueMessages = IntStream.range(0, 3)
            .mapToObj(x -> getGpSystemInboundQueueMessage())
            .collect(Collectors.toList());

        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(0), MESSAGE_1_TRANSACTION_TYPE, TRANSACTION_1_OPERATION_ID, fhirTN1);
        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(1), MESSAGE_1_TRANSACTION_TYPE, TRANSACTION_2_OPERATION_ID, fhirTN2);
        assertGpSystemInboundQueueMessages(
            softly, gpSystemInboundQueueMessages.get(2), MESSAGE_2_TRANSACTION_TYPE, TRANSACTION_3_OPERATION_ID, fhirTN3);
    }

    private void assertGpSystemInboundQueueMessages(
        SoftAssertions softly,
        Message message,
        ReferenceTransactionType.TransactionType expectedTransactionType,
        String expectedOperationId,
        Resource expectedFhir) throws JMSException, IOException {

        softly.assertThat(message.getStringProperty("OperationId"))
            .isEqualTo(expectedOperationId);
        softly.assertThat(message.getStringProperty("TransactionType"))
            .isEqualTo(expectedTransactionType.name().toLowerCase());
        softly.assertThat(parseTextMessage(message))
            .isEqualTo(new String(Files.readAllBytes(expectedFhir.getFile().toPath())));
    }

    private void assertInboundStates(SoftAssertions softly, List<InboundState> inboundStates) {
        softly.assertThat(inboundStates).hasSize(3);

        assertInboundState(
            softly, inboundStates.get(0), TRANSACTION_1_OPERATION_ID, SMS_1, TN_1, MESSAGE_1_TRANSACTION_TYPE, MESSAGE_1_TRANSLATION_TIMESTAMP);
        assertInboundState(
            softly, inboundStates.get(1), TRANSACTION_2_OPERATION_ID, SMS_1, TN_2, MESSAGE_1_TRANSACTION_TYPE, MESSAGE_1_TRANSLATION_TIMESTAMP);
        assertInboundState(
            softly, inboundStates.get(2), TRANSACTION_3_OPERATION_ID, SMS_2, TN_3, MESSAGE_2_TRANSACTION_TYPE, MESSAGE_2_TRANSLATION_TIMESTAMP);
    }

    private void assertInboundState(
        SoftAssertions softly,
        InboundState inboundStates,
        String expectedOperationId,
        Long expectedSMS,
        Long expectedTN,
        ReferenceTransactionType.TransactionType expectedTransactionType,
        Instant translationTimestamp) {

        var expectedInboundState = new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setOperationId(expectedOperationId)
            .setInterchangeSequence(SIS)
            .setMessageSequence(expectedSMS)
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setTransactionType(expectedTransactionType)
            .setTransactionNumber(expectedTN)
            .setTranslationTimestamp(translationTimestamp);
        softly.assertThat(inboundStates).isEqualToIgnoringGivenFields(expectedInboundState, "id");
    }

    private MeshMessage parseOutboundMessage(Message message) throws JMSException, JsonProcessingException {
        var body = InboundMeshService.readMessage(message);
        return objectMapper.readValue(body, MeshMessage.class);
    }
}
