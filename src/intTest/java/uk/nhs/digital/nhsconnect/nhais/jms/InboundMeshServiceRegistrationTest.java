package uk.nhs.digital.nhsconnect.nhais.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.service.InboundMeshService;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;
import uk.nhs.digital.nhsconnect.nhais.utils.OperationId;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class InboundMeshServiceRegistrationTest extends InboundMeshServiceBaseTest {

    private static final long INTERCHANGE_SEQUENCE = 3;
    private static final long MESSAGE_SEQUENCE = 4;
    private static final String SENDER = "TES5";
    private static final String RECIPIENT = "XX11";
    private static final long TRANSACTION_NUMBER = 18;
    private static final ReferenceTransactionType.TransactionType TRANSACTION_TYPE = ReferenceTransactionType.TransactionType.ACCEPTANCE;
    private static final String OPERATION_ID = OperationId.buildOperationId(SENDER, TRANSACTION_NUMBER);
    private static final Instant TRANSLATION_TIMESTAMP = ZonedDateTime
        .of(1992, 1, 14, 16, 19, 0, 0, TimestampService.UKZone)
        .toInstant();

    @Value("classpath:edifact/registration.dat")
    private Resource interchange;

    @Value("classpath:edifact/registration_recep.dat")
    private Resource recep;

    @Test
    @DirtiesContext
    void whenMeshInboundQueueRegistrationMessageIsReceived_thenMessageIsHandled(SoftAssertions softly) throws IOException, JMSException {
        var meshMessage = new MeshMessage()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setContent(new String(Files.readAllBytes(interchange.getFile().toPath())));

        Instant timeBeforeSending = Instant.now();

        sendToMeshInboundQueue(meshMessage);

        var inboundState = waitFor(
            () -> inboundStateRepository.findBy(WorkflowId.REGISTRATION, SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE));

        assertInboundState(softly, inboundState);

        assertGpSystemInboundQueueMessage(softly);

        assertOutboundQueueRecepMessage(softly, timeBeforeSending);
    }

    private void assertOutboundQueueRecepMessage(SoftAssertions softly, Instant timeBeforeSending) throws JMSException, IOException {
        var gpSystemInboundQueueMessage = getOutboundQueueRecepMessage();

        var meshMessage = parseOutboundMessage(gpSystemInboundQueueMessage);

        softly.assertThat(meshMessage.getContent()).isEqualTo(new String(Files.readAllBytes(recep.getFile().toPath())));
        softly.assertThat(meshMessage.getWorkflowId()).isEqualTo(WorkflowId.RECEP);
        var messageSentTimestamp = new TimestampService().parseFromISO(meshMessage.getMessageSentTimestamp());
        assertThat(messageSentTimestamp.isAfter(timeBeforeSending)).isTrue();
        assertThat(messageSentTimestamp.isBefore(Instant.now())).isTrue();
        //TODO: other assertions on recep message
    }

    private void assertGpSystemInboundQueueMessage(SoftAssertions softly) throws JMSException {
        var gpSystemInboundQueueMessage = getGpSystemInboundQueueMessage();

        softly.assertThat(gpSystemInboundQueueMessage.getStringProperty(JmsHeaders.OPERATION_ID)).isEqualTo(OPERATION_ID);

        var resource = parseInboundMessage(gpSystemInboundQueueMessage);
        softly.assertThat(resource).isExactlyInstanceOf(Parameters.class);
        //TODO: other assertions on FHIR message
    }

    private void assertInboundState(SoftAssertions softly, InboundState inboundState) {
        var expectedInboundState = new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setOperationId(OPERATION_ID)
            .setReceiveInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setReceiveMessageSequence(MESSAGE_SEQUENCE)
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setTransactionType(TRANSACTION_TYPE)
            .setTransactionNumber(TRANSACTION_NUMBER)
            .setTranslationTimestamp(TRANSLATION_TIMESTAMP);
        softly.assertThat(inboundState).isEqualToIgnoringGivenFields(expectedInboundState, "id");
    }

    @SneakyThrows
    private Message getGpSystemInboundQueueMessage() {
        return jmsTemplate.receive(gpSystemInboundQueueName);
    }

    @SneakyThrows
    private Message getOutboundQueueRecepMessage() {
        return jmsTemplate.receive(meshOutboundQueueName);
    }

    private IBaseResource parseInboundMessage(Message message) throws JMSException {
        if (message == null) {
            return null;
        }
        var body = InboundMeshService.readMessage(message);
        return new FhirParser().parse(body);
    }

    private MeshMessage parseOutboundMessage(Message message) throws JMSException, JsonProcessingException {
        var body = InboundMeshService.readMessage(message);
        return objectMapper.readValue(body, MeshMessage.class);
    }
}
