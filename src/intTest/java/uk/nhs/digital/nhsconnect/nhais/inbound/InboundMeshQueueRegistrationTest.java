package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import javax.jms.JMSException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.when;

/**
 * Tests the processing of a REGISTRATION interchange by publishing it onto the inbound MESH message queue. This bypasses the
 * MESH polling loop / MESH Client / MESH API.
 */
@DirtiesContext
public class InboundMeshQueueRegistrationTest extends IntegrationBaseTest {

    private static final String SENDER = "XX11";
    private static final String RECIPIENT = "TES5";
    private static final long SIS = 3;
    private static final long SMS = 9;
    private static final long TN = 13;
    private static final ReferenceTransactionType.Inbound TRANSACTION_TYPE = ReferenceTransactionType.Inbound.APPROVAL;
    private static final String OPERATION_ID = OperationId.buildOperationId(RECIPIENT, TN);
    private static final Instant TRANSLATION_TIMESTAMP = ZonedDateTime
        .of(2020, 1, 25, 12, 35, 0, 0, TimestampService.UKZone)
        .toInstant();
    private static final Instant GENERATED_TIMESTAMP = ZonedDateTime.of(2020, 6, 10, 14, 38, 00, 0, TimestampService.UKZone)
        .toInstant();
    private static final String ISO_GENERATED_TIMESTAMP = new TimestampService().formatInISO(GENERATED_TIMESTAMP);

    @MockBean
    private TimestampService timestampService;
    @Value("classpath:edifact/registration.dat")
    private Resource interchange;
    @Value("classpath:edifact/registration_recep.dat")
    private Resource recep;
    @Value("classpath:edifact/registration.json")
    private Resource fhir;

    @BeforeEach
    void setUp() {
        when(timestampService.getCurrentTimestamp()).thenReturn(GENERATED_TIMESTAMP);
        when(timestampService.formatInISO(GENERATED_TIMESTAMP)).thenReturn(ISO_GENERATED_TIMESTAMP);
        clearGpSystemInboundQueue();
        clearMeshMailboxes();
    }

    @Test
    void whenMeshInboundQueueRegistrationMessageIsReceived_thenMessageIsHandled(SoftAssertions softly) throws IOException, JMSException {
        var meshMessage = new MeshMessage()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setContent(new String(Files.readAllBytes(interchange.getFile().toPath())))
            .setMeshMessageId("12345");

        sendToMeshInboundQueue(meshMessage);

        assertInboundState(softly);
        assertGpSystemInboundQueueMessage(softly);
        assertOutboundRecepMessage(softly);
        assertOutboundState(softly);
    }

    private void assertOutboundRecepMessage(SoftAssertions softly) throws IOException {
        var meshMessage = waitForMeshMessage(nhaisMeshClient);

        softly.assertThat(meshMessage.getContent()).isEqualTo(new String(Files.readAllBytes(recep.getFile().toPath())));
        softly.assertThat(meshMessage.getWorkflowId()).isEqualTo(WorkflowId.RECEP);
        // timestamp not set for outbound recep messages
        softly.assertThat(meshMessage.getMessageSentTimestamp()).isNull();
    }

    private void assertGpSystemInboundQueueMessage(SoftAssertions softly) throws JMSException, IOException {
        var message = getGpSystemInboundQueueMessage();
        var content = parseTextMessage(message).replaceAll("\\s+","");
        var expectedContent = new String(Files.readAllBytes(fhir.getFile().toPath())).replaceAll("\\s+","");

        softly.assertThat(message.getStringProperty("OperationId")).isEqualTo(OPERATION_ID);
        softly.assertThat(message.getStringProperty("TransactionType")).isEqualTo(TRANSACTION_TYPE.name().toLowerCase());
        softly.assertThat(content).isEqualTo(expectedContent);
    }

    private void assertInboundState(SoftAssertions softly) {
        var inboundState = waitFor(
            () -> inboundStateRepository
                .findBy(WorkflowId.REGISTRATION, SENDER, RECIPIENT, SIS, SMS, TN)
                .orElse(null));

        var expectedInboundState = new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setOperationId(OPERATION_ID)
            .setInterchangeSequence(SIS)
            .setMessageSequence(SMS)
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setTransactionType(TRANSACTION_TYPE)
            .setTransactionNumber(TN)
            .setTranslationTimestamp(TRANSLATION_TIMESTAMP)
            .setProcessedTimestamp(GENERATED_TIMESTAMP);
        softly.assertThat(inboundState).isEqualToIgnoringGivenFields(expectedInboundState, "id");
    }

    private void assertOutboundState(SoftAssertions softly) {
        Iterable<OutboundState> outboundStates = outboundStateRepository.findAll();

        softly.assertThat(outboundStates).hasSize(1);
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
