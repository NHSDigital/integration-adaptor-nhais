package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTransactionNumberTest {

    private static final long TRANSACTION_NUMBER = 1234L;
    private static final long MAX_TRANSACTION_SEQUENCE_NUMBER = 10_000_000L;
    private static final long MAX_SEQUENCE_NUMBER = 99_999_999L;

    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        ReferenceTransactionNumber referenceTransactionNumber =
                new ReferenceTransactionNumber();
        referenceTransactionNumber.setTransactionNumber(TRANSACTION_NUMBER);
        String edifact = referenceTransactionNumber.toEdifact();

        assertEquals("RFF+TN:1234'", edifact);
    }

    @Test
    public void testValidationStatefulMinMaxTransactionNumber() throws EdifactValidationException {
        var transactionNumber = new ReferenceTransactionNumber();

        transactionNumber.setTransactionNumber(0L);
        assertThatThrownBy(transactionNumber::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute transactionNumber must be between 1 and 9999999");

        transactionNumber.setTransactionNumber(MAX_TRANSACTION_SEQUENCE_NUMBER);
        assertThatThrownBy(transactionNumber::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute transactionNumber must be between 1 and 9999999");

        transactionNumber.setTransactionNumber(1L);
        transactionNumber.validateStateful();

        transactionNumber.setTransactionNumber(MAX_SEQUENCE_NUMBER);
        transactionNumber.validateStateful();
    }

    @Test
    void testFromString() {
        ReferenceTransactionNumber referenceTransactionNumber =
            new ReferenceTransactionNumber();
        referenceTransactionNumber.setTransactionNumber(TRANSACTION_NUMBER);

        assertThat(ReferenceTransactionNumber.fromString("RFF+TN:1234").getValue()).isEqualTo(referenceTransactionNumber.getValue());
        assertThatThrownBy(() -> ReferenceTransactionNumber.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
