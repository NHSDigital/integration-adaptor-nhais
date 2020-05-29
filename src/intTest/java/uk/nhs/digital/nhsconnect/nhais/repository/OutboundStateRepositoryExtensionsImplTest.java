package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.container.MongoDbInitializer;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EntityNotFoundException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;
import uk.nhs.digital.nhsconnect.nhais.service.TimestampService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(initializers = MongoDbInitializer.class)
@SpringBootTest
@DirtiesContext
public class OutboundStateRepositoryExtensionsImplTest {

    private static final String SENDER = "some_sender";
    private static final String OTHER_SENDER = "some_other_sender";
    private static final String RECIPIENT = "some_recipient";
    private static final Long INTERCHANGE_SEQUENCE = 123L;
    private static final Long MESSAGE_SEQUENCE = 234L;
    private static final ReferenceMessageRecep.RecepCode RECEP_CODE = ReferenceMessageRecep.RecepCode.CA;
    private static final Instant RECEP_DATE_TIME = new TimestampService().getCurrentTimestamp();

    @Autowired
    OutboundStateRepository outboundStateRepository;

    @Test
    void whenUpdatingRecep_thenRecepDetailsAreUpdated() {

        var outboundState = new OutboundState()
            .setSender(SENDER)
            .setRecipient(RECIPIENT)
            .setSendInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setSendMessageSequence(MESSAGE_SEQUENCE);

        var otherOutboundState = new OutboundState()
            .setSender(OTHER_SENDER)
            .setRecipient(RECIPIENT)
            .setSendInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setSendMessageSequence(MESSAGE_SEQUENCE);

        outboundState = outboundStateRepository.save(outboundState);
        otherOutboundState = outboundStateRepository.save(otherOutboundState);

        assertNull(outboundState.getRecepCode());
        assertNull(outboundState.getRecepDateTime());

        assertNull(otherOutboundState.getRecepCode());
        assertNull(otherOutboundState.getRecepDateTime());

        outboundStateRepository.updateRecep(
            SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE,
            RECEP_CODE, RECEP_DATE_TIME);

        outboundState = outboundStateRepository.findById(outboundState.getId()).orElseThrow();
        otherOutboundState = outboundStateRepository.findById(otherOutboundState.getId()).orElseThrow();

        assertEquals(outboundState.getRecepCode(), RECEP_CODE);
        assertEquals(outboundState.getRecepDateTime(), RECEP_DATE_TIME);

        assertNull(otherOutboundState.getRecepCode());
        assertNull(otherOutboundState.getRecepDateTime());
    }

    @Test
    void whenUpdatingNonExistingEntity_thenThrowsExceptionthren() {
        assertThrows(
            EntityNotFoundException.class,
            () -> outboundStateRepository.updateRecep(
                SENDER, RECIPIENT, INTERCHANGE_SEQUENCE, MESSAGE_SEQUENCE,
                RECEP_CODE, RECEP_DATE_TIME));
    }
}
