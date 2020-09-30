package uk.nhs.digital.nhsconnect.nhais.outbound.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepositoryExtensions;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@DirtiesContext
public class OutboundStateRepositoryExtensionsImplTest {

    private static final String SENDER = "some_sender";
    private static final String NON_EXISTING_SENDER = "non_existing_sender";
    private static final String OTHER_SENDER = "some_other_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final Long INTERCHANGE_SEQUENCE = 123L;
    private static final Long MESSAGE_SEQUENCE = 234L;
    private static final ReferenceMessageRecep.RecepCode RECEP_CODE = ReferenceMessageRecep.RecepCode.ERROR;
    private static final Instant RECEP_TRANSLATION_TIMESTAMP = Instant.ofEpochSecond(1601293246L);
    private static final Instant RECEP_PROCESSED_TIMESTAMP = Instant.ofEpochSecond(1601292246L);
    private static final Long RECEP_INTERCHANGE_SEQUENCE = 9999L;

    @Autowired
    private OutboundStateRepository outboundStateRepository;

    @Test
    void whenUpdatingRecep_thenRecepDetailsAreUpdated() {

        var outboundState = new OutboundState()
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setMessageSequence(MESSAGE_SEQUENCE);

        var otherOutboundState = new OutboundState()
            .setSender(OTHER_SENDER)
            .setRecipient(RECIPIENT)
            .setInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setMessageSequence(MESSAGE_SEQUENCE);

        outboundState = outboundStateRepository.save(outboundState);
        otherOutboundState = outboundStateRepository.save(otherOutboundState);

        assertThat(outboundState.getRecep()).isNull();

        var recep = new OutboundState.Recep()
            .setInterchangeSequence(RECEP_INTERCHANGE_SEQUENCE)
            .setProcessedTimestamp(RECEP_PROCESSED_TIMESTAMP)
            .setTranslationTimestamp(RECEP_TRANSLATION_TIMESTAMP)
            .setCode(RECEP_CODE);
        outboundStateRepository.updateRecepDetails(
            new OutboundStateRepositoryExtensions.UpdateRecepParams(
                SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE, recep));

        outboundState = outboundStateRepository.findById(outboundState.getId()).orElseThrow();
        otherOutboundState = outboundStateRepository.findById(otherOutboundState.getId()).orElseThrow();

        assertThat(outboundState.getRecep()).isEqualTo(recep);

        assertThat(otherOutboundState.getRecep()).isNull();
    }

    @Test
    void whenUpdatingNonExistingEntity_thenReturnsEmpty() {
        var recep = new OutboundState.Recep()
            .setInterchangeSequence(RECEP_INTERCHANGE_SEQUENCE)
            .setProcessedTimestamp(RECEP_PROCESSED_TIMESTAMP)
            .setTranslationTimestamp(RECEP_TRANSLATION_TIMESTAMP)
            .setCode(RECEP_CODE);
        var updateRecepParams = new OutboundStateRepositoryExtensions.UpdateRecepParams(
            NON_EXISTING_SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE, recep);

        assertThat(outboundStateRepository.updateRecepDetails(updateRecepParams)).isEmpty();
    }
}
