package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.nhsconnect.nhais.exceptions.SenderAndRecipientValidationException;
import uk.nhs.digital.nhsconnect.nhais.repository.SequenceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SequenceServiceTest {
    private final static String TRANSACTION_ID = "transaction_id";
    private final static String INTERCHANGE_ID = "SIS-sender-recipient";
    private final static String MESSAGE_ID = "SMS-sender-recipient";
    private final static Long SEQ_VALUE = 1L;

    private SequenceService sequenceService;

    @Mock
    private SequenceRepository sequenceRepository;

    @Before
    public void setUp() {
        when(sequenceRepository.getNext(TRANSACTION_ID)).thenReturn(SEQ_VALUE);
        when(sequenceRepository.getNext(INTERCHANGE_ID)).thenReturn(SEQ_VALUE);
        when(sequenceRepository.getNext(MESSAGE_ID)).thenReturn(SEQ_VALUE);
        sequenceService = new SequenceService(sequenceRepository);
    }

    @Test
    public void When_GenerateTransactionId_Expect_CorrectValue() {
        assertThat(sequenceService.generateTransactionId()).isEqualTo(String.valueOf(SEQ_VALUE));
    }

    @Test
    public void When_GenerateInterchangeId_Expect_CorrectValue() {
        assertThat(sequenceService.generateInterchangeId("sender", "recipient"))
                .isEqualTo(String.valueOf(SEQ_VALUE));
    }

    @Test
    public void When_generateMessageId_Expect_ResetValue() {
        assertThat(sequenceService.generateMessageId("sender", "recipient"))
                .isEqualTo(String.valueOf(SEQ_VALUE));
    }

    @Test(expected = SenderAndRecipientValidationException.class)
    public void When_GenerateIdsForInvalidSender_Expect_Exception() {
        sequenceService.generateInterchangeId(null, "recipient");
    }

    @Test(expected = SenderAndRecipientValidationException.class)
    public void When_GenerateIdsForInvalidRecipient_Expect_Exception() {
        sequenceService.generateInterchangeId("sender", "");
    }
}