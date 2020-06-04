package uk.nhs.digital.nhsconnect.nhais.jms;

import com.google.common.collect.Lists;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.repository.DataType;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class InboundMeshServiceRecepTest extends InboundMeshServiceBaseTest {

    private static final long INTERCHANGE_SEQUENCE = 64;
    private static final long REF_INTERCHANGE_SEQUENCE_1 = 1;
    private static final long REF_MESSAGE_SEQUENCE_1 = 100;
    private static final long REF_MESSAGE_SEQUENCE_2 = 200;
    private static final String SENDER = "FHS1";
    private static final String RECIPIENT = "GP05";
    private static final Instant TRANSLATION_TIMESTAMP = ZonedDateTime
        .of(1993, 5, 19, 6, 0, 0, 0, TimestampService.UKZone)
        .toInstant();

    @Value("classpath:edifact/recep.dat")
    private Resource recep;

    @Test
    @DirtiesContext
    void whenMeshInboundQueueRecepMessageIsReceived_thenRecepHandled(SoftAssertions softly) throws IOException {
        createOutboundStateRecords();

        sendToMeshInboundQueue(new MeshMessage()
            .setWorkflowId(WorkflowId.RECEP)
            .setContent(new String(Files.readAllBytes(recep.getFile().toPath()))));

        var inboundState = waitForInboundState(DataType.RECEP, SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, null);

        assertInboundState(softly, inboundState);

        assertOutboundStateRecepUpdates(softly);
    }

    private void assertOutboundStateRecepUpdates(SoftAssertions softly) {
        var expectedOutboundStateRef1 = new OutboundState()
            .setDataType(DataType.RECEP)
            .setSendInterchangeSequence(REF_INTERCHANGE_SEQUENCE_1)
            .setSendMessageSequence(REF_MESSAGE_SEQUENCE_1)
            .setSender(RECIPIENT)
            .setRecipient(SENDER)
            .setRecepCode(ReferenceMessageRecep.RecepCode.SUCCESS)
            .setRecepDateTime(TRANSLATION_TIMESTAMP);
        var expectedOutboundStateRef2 = new OutboundState()
            .setDataType(DataType.RECEP)
            .setSendInterchangeSequence(REF_INTERCHANGE_SEQUENCE_1)
            .setSendMessageSequence(REF_MESSAGE_SEQUENCE_2)
            .setSender(RECIPIENT)
            .setRecipient(SENDER)
            .setRecepCode(ReferenceMessageRecep.RecepCode.ERROR)
            .setRecepDateTime(TRANSLATION_TIMESTAMP);

        var outboundStates = waitForOutboundStateRecepUpdate();
        var outboundStateRef1 = outboundStates.get(0);
        var outboundStateRef2 = outboundStates.get(1);

        softly.assertThat(outboundStateRef1).isEqualToIgnoringGivenFields(expectedOutboundStateRef1, "id");
        softly.assertThat(outboundStateRef2).isEqualToIgnoringGivenFields(expectedOutboundStateRef2, "id");
    }

    private void assertInboundState(SoftAssertions softly, InboundState inboundState) {
        var expectedInboundState = new InboundState()
            .setDataType(DataType.RECEP)
            .setReceiveInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setTranslationTimestamp(TRANSLATION_TIMESTAMP);

        softly.assertThat(inboundState).isEqualToIgnoringGivenFields(expectedInboundState, "id");
    }

    private void createOutboundStateRecords() {
        outboundStateRepository.save(new OutboundState()
            .setDataType(DataType.RECEP)
            .setSendInterchangeSequence(REF_INTERCHANGE_SEQUENCE_1)
            .setSendMessageSequence(REF_MESSAGE_SEQUENCE_1)
            .setSender(RECIPIENT)
            .setRecipient(SENDER));
        outboundStateRepository.save(new OutboundState()
            .setDataType(DataType.RECEP)
            .setSendInterchangeSequence(REF_INTERCHANGE_SEQUENCE_1)
            .setSendMessageSequence(REF_MESSAGE_SEQUENCE_2)
            .setSender(RECIPIENT)
            .setRecipient(SENDER));
    }

    protected List<OutboundState> waitForOutboundStateRecepUpdate() {
        Supplier<List<OutboundState>> getData = () -> Lists.newArrayList(outboundStateRepository.findAll());

        await()
            .atMost(WAIT_FOR_IN_SECONDS, SECONDS)
            .pollInterval(50, MILLISECONDS)
            .until(() -> getData.get().stream().filter(state -> state.getRecepCode() != null).count() == 2);

        return getData.get();
    }
}
