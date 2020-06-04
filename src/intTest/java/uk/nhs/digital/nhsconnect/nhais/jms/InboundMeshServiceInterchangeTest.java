package uk.nhs.digital.nhsconnect.nhais.jms;

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
import uk.nhs.digital.nhsconnect.nhais.repository.DataType;
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

public class InboundMeshServiceInterchangeTest extends InboundMeshServiceBaseTest {

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

    @Value("classpath:edifact/interchange.dat")
    private Resource edifact;

    @Test
    @DirtiesContext
    void whenMeshInboundQueueInterchangeMessageIsReceived_thenInboundStateIsSavedAndTranslatedDataPushedToGbInboundQueue(SoftAssertions softly) throws IOException, JMSException {
        var meshMessage = new MeshMessage()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setContent(new String(Files.readAllBytes(edifact.getFile().toPath())));

        sendToMeshInboundQueue(meshMessage);

        var inboundState = waitForInboundState(DataType.INTERCHANGE, SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE);
        var gpSystemInboundQueueMessage = getGpSystemInboundQueueMessage();

        assertInboundState(softly, inboundState);

        assertGpSystemInboundQueueMessage(softly, gpSystemInboundQueueMessage);
    }

    private void assertGpSystemInboundQueueMessage(SoftAssertions softly, Message gpSystemInboundQueueMessage) throws JMSException {
        softly.assertThat(gpSystemInboundQueueMessage.getStringProperty(JmsHeaders.OPERATION_ID)).isEqualTo(OPERATION_ID);
        var resource = parseMessage(gpSystemInboundQueueMessage);
        softly.assertThat(resource).isExactlyInstanceOf(Parameters.class);
        //TODO: other assertions on queue message
    }

    private void assertInboundState(SoftAssertions softly, InboundState inboundState) {
        var expectedInboundState = new InboundState()
            .setDataType(DataType.INTERCHANGE)
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

    private IBaseResource parseMessage(Message message) throws JMSException {
        if (message == null) {
            return null;
        }
        var body = InboundMeshService.readMessage(message);
        return new FhirParser().parse(body);
    }
}
