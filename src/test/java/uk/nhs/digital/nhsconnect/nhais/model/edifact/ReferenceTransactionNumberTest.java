package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTransactionNumberTest {
    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        ReferenceTransactionNumber referenceTransactionNumber =
                new ReferenceTransactionNumber();
        referenceTransactionNumber.setTransactionNumber(1234L);
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

        transactionNumber.setTransactionNumber(10_000_000L);
        assertThatThrownBy(transactionNumber::validateStateful)
            .isInstanceOf(EdifactValidationException.class)
            .hasMessage("RFF: Attribute transactionNumber must be between 1 and 9999999");

        transactionNumber.setTransactionNumber(1L);
        transactionNumber.validateStateful();

        transactionNumber.setTransactionNumber(9_999_999L);
        transactionNumber.validateStateful();
    }

    @Test
    void testFromString() {
        ReferenceTransactionNumber referenceTransactionNumber =
            new ReferenceTransactionNumber();
        referenceTransactionNumber.setTransactionNumber(1234L);

        assertThat(ReferenceTransactionNumber.fromString("RFF+TN:1234").getValue()).isEqualTo(referenceTransactionNumber.getValue());
        assertThatThrownBy(() -> ReferenceTransactionNumber.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
