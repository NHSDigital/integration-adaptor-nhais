package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EntityNotFoundException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
public class OutboundStateRepositoryExtensionsImplTest {

    private static final String SENDER = "some_sender";
    private static final String NON_EXISTING_SENDER = "non_existing_sender";
    private static final String OTHER_SENDER = "some_other_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final Long INTERCHANGE_SEQUENCE = 123L;
    private static final Long MESSAGE_SEQUENCE = 234L;
    private static final ReferenceMessageRecep.RecepCode RECEP_CODE = ReferenceMessageRecep.RecepCode.ERROR;
    private static final Instant RECEP_DATE_TIME = Instant.ofEpochMilli(123123);

    @Autowired
    OutboundStateRepository outboundStateRepository;

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

        assertThat(outboundState.getRecepCode()).isNull();
        assertThat(outboundState.getRecepDateTime()).isNull();

        assertThat(otherOutboundState.getRecepCode()).isNull();
        assertThat(otherOutboundState.getRecepDateTime()).isNull();

        outboundStateRepository.updateRecepDetails(
            new OutboundStateRepositoryExtensions.UpdateRecepDetailsQueryParams(
                SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE),
            new OutboundStateRepositoryExtensions.UpdateRecepDetails(
                RECEP_CODE, RECEP_DATE_TIME));

        outboundState = outboundStateRepository.findById(outboundState.getId()).orElseThrow();
        otherOutboundState = outboundStateRepository.findById(otherOutboundState.getId()).orElseThrow();

        assertThat(outboundState.getRecepCode()).isEqualTo(RECEP_CODE);
        assertThat(outboundState.getRecepDateTime()).isEqualTo(RECEP_DATE_TIME);

        assertThat(otherOutboundState.getRecepCode()).isNull();
        assertThat(otherOutboundState.getRecepDateTime()).isNull();
    }

    @Test
    void whenUpdatingNonExistingEntity_thenThrowsException() {
        assertThatThrownBy(
            () -> outboundStateRepository.updateRecepDetails(
                new OutboundStateRepositoryExtensions.UpdateRecepDetailsQueryParams(
                    NON_EXISTING_SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE),
                new OutboundStateRepositoryExtensions.UpdateRecepDetails(
                    RECEP_CODE, RECEP_DATE_TIME)))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
