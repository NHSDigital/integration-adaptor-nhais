package uk.nhs.digital.nhsconnect.nhais.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.nhsconnect.nhais.exceptions.SenderAndRecipientValidationException;
import uk.nhs.digital.nhsconnect.nhais.repository.SequenceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SequenceServiceTest {
    private final static String TRANSACTION_ID = "transaction_id";
    private final static Long NEXT_VALUE = 10L;
    private final static Long NEW_VALUE = 1L;

    private SequenceService sequenceService;

    @Mock
    private SequenceRepository sequenceRepository;

    @Before
    public void setUp() {
        when(sequenceRepository.existsByKey(TRANSACTION_ID)).thenReturn(true);
        when(sequenceRepository.getNext(TRANSACTION_ID)).thenReturn(NEXT_VALUE);
        when(sequenceRepository.addSequenceKey(anyString())).thenReturn(NEW_VALUE);

        sequenceService = new SequenceService(sequenceRepository);
    }

    @Test
    public void When_GenerateTransactionId_Expect_CorrectValue() {
        assertThat(sequenceService.generateTransactionId()).isEqualTo(String.valueOf(NEXT_VALUE));
    }

    @Test
    public void When_GenerateInterchangeId_Expect_CorrectValue() {
        assertThat(sequenceService.generateInterchangeId("sender", "recipient"))
                .isEqualTo(String.valueOf(NEW_VALUE));
    }

    @Test
    public void When_generateMessageId_Expect_ResetValue() {
        assertThat(sequenceService.generateMessageId("sender", "recipient"))
                .isEqualTo(String.valueOf(NEW_VALUE));
    }

    @Test(expected = SenderAndRecipientValidationException.class)
    public void When_GenerateIdsForInvalidSender_Expect_Exception() {
        assertThat(sequenceService.generateInterchangeId(null, "recipient"))
                .isEqualTo(String.valueOf(NEW_VALUE));
    }

    @Test(expected = SenderAndRecipientValidationException.class)
    public void When_GenerateIdsForInvalidRecipient_Expect_Exception() {
        assertThat(sequenceService.generateInterchangeId("sender", ""))
                .isEqualTo(String.valueOf(NEW_VALUE));
    }
}