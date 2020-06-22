package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.exceptions.SenderAndRecipientValidationException;
import uk.nhs.digital.nhsconnect.nhais.exceptions.SenderValidationException;
import uk.nhs.digital.nhsconnect.nhais.repository.SequenceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SequenceServiceTest {
    private final static String TRANSACTION_ID = "transaction_id";
    private final static String INTERCHANGE_ID = "SIS-sender-recipient";
    private final static String MESSAGE_ID = "SMS-sender-recipient";
    private final static Long SEQ_VALUE = 1L;

    @InjectMocks
    private SequenceService sequenceService;

    @Mock
    private SequenceRepository sequenceRepository;

    @Test
    public void When_GenerateTransactionId_Expect_CorrectValue() {
        String key = TRANSACTION_ID + "-sender";
        when(sequenceRepository.getNextForTransaction(key)).thenReturn(SEQ_VALUE);
        assertThat(sequenceService.generateTransactionId("sender")).isEqualTo(SEQ_VALUE);
    }

    @Test
    public void When_GenerateInterchangeId_Expect_CorrectValue() {
        when(sequenceRepository.getNext(INTERCHANGE_ID)).thenReturn(SEQ_VALUE);
        assertThat(sequenceService.generateInterchangeId("sender", "recipient"))
                .isEqualTo(SEQ_VALUE);
    }

    @Test
    public void When_generateMessageId_Expect_ResetValue() {
        when(sequenceRepository.getNext(MESSAGE_ID)).thenReturn(SEQ_VALUE);
        assertThat(sequenceService.generateMessageId("sender", "recipient"))
                .isEqualTo(SEQ_VALUE);
    }

    @Test
    public void When_GenerateIdsForInvalidSender_Expect_Exception() {
        assertThrows(SenderAndRecipientValidationException.class, () ->
                sequenceService.generateInterchangeId(null, "recipient"));
    }

    @Test
    public void When_GenerateIdsForInvalidRecipient_Expect_Exception() {
        assertThrows(SenderAndRecipientValidationException.class, () ->
                sequenceService.generateInterchangeId("sender", ""));
    }

    @Test
    public void When_GenerateTransactionIdsForInvalidSender_Expect_Exception() {
        assertThrows(SenderValidationException.class, () ->
            sequenceService.generateTransactionId(null));
    }
}