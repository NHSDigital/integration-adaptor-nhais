package uk.nhs.digital.nhsconnect.nhais.inbound;

import com.google.common.collect.Lists;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.IntegrationBaseTest;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.when;

/**
 * Tests the processing of a RECEP interchange by publishing it onto the inbound MESH message queue. This bypasses the
 * MESH polling loop / MESH Client / MESH API.
 */
@DirtiesContext
public class InboundMeshQueueRecepTest extends IntegrationBaseTest {

    private static final long INTERCHANGE_SEQUENCE = 64;
    private static final long MESSAGE_SEQUENCE = 28;
    private static final long REF_INTERCHANGE_SEQUENCE_1 = 1;
    private static final long REF_MESSAGE_SEQUENCE_1 = 100;
    private static final long REF_MESSAGE_SEQUENCE_2 = 200;
    private static final String SENDER = "FHS1";
    private static final String RECIPIENT = "GP05";
    private static final Instant TRANSLATION_TIMESTAMP = ZonedDateTime
        .of(2020, 6, 20, 14, 0, 0, 0, TimestampService.UKZone)
        .toInstant();
    // Mongo only supports millis precision
    private static final Instant PROCESSED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final String CONVERSATION_ID = "ABC123";

    @Value("classpath:edifact/recep.dat")
    private Resource recep;

    @MockBean
    private TimestampService timestampService;

    @MockBean
    private ConversationIdService conversationIdService;

    @BeforeEach
    void setUp() {
        when(timestampService.getCurrentTimestamp()).thenReturn(PROCESSED_TIMESTAMP);
        when(conversationIdService.getCurrentConversationId()).thenReturn(CONVERSATION_ID);
        clearGpSystemInboundQueue();
        clearMeshMailboxes();
    }

    @Test
    void whenMeshInboundQueueRecepMessageIsReceived_thenRecepHandled(SoftAssertions softly) throws IOException {
        createOutboundStateRecords();

        sendToMeshInboundQueue(new MeshMessage()
            .setWorkflowId(WorkflowId.RECEP)
            .setContent(new String(Files.readAllBytes(recep.getFile().toPath())))
            .setMeshMessageId("12345"));

        assertInboundState(softly);
        assertOutboundStateRecepUpdates(softly);
    }

    private void assertOutboundStateRecepUpdates(SoftAssertions softly) {
        var expectedOutboundStateRef1 = buildExpectedOutboundState(REF_MESSAGE_SEQUENCE_1, ReferenceMessageRecep.RecepCode.SUCCESS);
        var expectedOutboundStateRef2 = buildExpectedOutboundState(REF_MESSAGE_SEQUENCE_2, ReferenceMessageRecep.RecepCode.ERROR);

        var outboundStates = waitFor(() -> {
            var all = Lists.newArrayList(outboundStateRepository.findAll());
            if (all.stream().allMatch(outboundState -> outboundState.getRecep() != null)) {
                return all;
            }
            return null;
        });
        var outboundStateRef1 = outboundStates.get(0);
        var outboundStateRef2 = outboundStates.get(1);

        softly.assertThat(outboundStateRef1).isEqualToIgnoringGivenFields(expectedOutboundStateRef1, "id");
        softly.assertThat(outboundStateRef2).isEqualToIgnoringGivenFields(expectedOutboundStateRef2, "id");
    }

    private OutboundState buildExpectedOutboundState(long refMessageSequence1, ReferenceMessageRecep.RecepCode success) {
        return buildOutboundState(refMessageSequence1)
            .setRecep(new OutboundState.Recep()
                .setCode(success)
                .setTranslationTimestamp(TRANSLATION_TIMESTAMP)
                .setProcessedTimestamp(PROCESSED_TIMESTAMP)
                .setInterchangeSequence(INTERCHANGE_SEQUENCE));
    }

    private void assertInboundState(SoftAssertions softly) {
        var inboundState = waitFor(
            () -> inboundStateRepository
                .findBy(WorkflowId.RECEP, SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE, null)
                .orElse(null));

        var expectedInboundState = new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setMessageSequence(MESSAGE_SEQUENCE)
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setTranslationTimestamp(TRANSLATION_TIMESTAMP)
            .setProcessedTimestamp(PROCESSED_TIMESTAMP)
            .setConversationId(CONVERSATION_ID);

        softly.assertThat(inboundState).isEqualToIgnoringGivenFields(expectedInboundState, "id");
    }

    private void createOutboundStateRecords() {
        outboundStateRepository.save(buildOutboundState(REF_MESSAGE_SEQUENCE_1));
        outboundStateRepository.save(buildOutboundState(REF_MESSAGE_SEQUENCE_2));
    }

    private OutboundState buildOutboundState(long refMessageSequence1) {
        return new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setInterchangeSequence(REF_INTERCHANGE_SEQUENCE_1)
            .setMessageSequence(refMessageSequence1)
            .setSender(RECIPIENT)
            .setRecipient(SENDER);
    }
}
