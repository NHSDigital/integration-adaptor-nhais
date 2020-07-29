package uk.nhs.digital.nhsconnect.nhais.sequence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SequenceServiceTest {
    private final static String TRANSACTION_ID = "TN-sender";
    private final static String INTERCHANGE_ID = "SIS-sender-recipient";
    private final static String MESSAGE_ID = "SMS-sender-recipient";
    private final static Long SEQ_VALUE = 1L;

    @InjectMocks
    private SequenceService sequenceService;

    @Mock
    private SequenceRepository sequenceRepository;

    @Test
    public void When_GenerateTransactionId_Expect_CorrectValue() {
        when(sequenceRepository.getNextForTransaction(TRANSACTION_ID)).thenReturn(SEQ_VALUE);
        assertThat(sequenceService.generateTransactionNumber("sender")).isEqualTo(SEQ_VALUE);
    }

    @Test
    public void When_GenerateInterchangeId_Expect_CorrectValue() {
        when(sequenceRepository.getNext(INTERCHANGE_ID)).thenReturn(SEQ_VALUE);
        assertThat(sequenceService.generateInterchangeSequence("sender", "recipient"))
                .isEqualTo(SEQ_VALUE);
    }

    @Test
    public void When_generateMessageId_Expect_ResetValue() {
        when(sequenceRepository.getNext(MESSAGE_ID)).thenReturn(SEQ_VALUE);
        assertThat(sequenceService.generateMessageSequence("sender", "recipient"))
                .isEqualTo(SEQ_VALUE);
    }

    @Test
    public void When_GenerateIdsForInvalidSender_Expect_Exception() {
        assertThatThrownBy(() -> sequenceService.generateInterchangeSequence(null, "recipient"))
            .isExactlyInstanceOf(SequenceException.class);
    }

    @Test
    public void When_GenerateIdsForInvalidRecipient_Expect_Exception() {
        assertThatThrownBy(() -> sequenceService.generateInterchangeSequence("sender", ""))
            .isExactlyInstanceOf(SequenceException.class);
    }

    @Test
    public void When_GenerateTransactionIdsForNullSender_Expect_Exception() {
        assertThatThrownBy(() -> sequenceService.generateTransactionNumber(null))
            .isExactlyInstanceOf(SequenceException.class);
    }

    @Test
    public void When_GenerateTransactionIdsForEmptySender_Expect_Exception() {
        assertThatThrownBy(() -> sequenceService.generateTransactionNumber(""))
            .isExactlyInstanceOf(SequenceException.class);
    }
}