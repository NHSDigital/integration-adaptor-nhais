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
    void testFromString() {
        ReferenceTransactionNumber referenceTransactionNumber =
            new ReferenceTransactionNumber();
        referenceTransactionNumber.setTransactionNumber(1234L);

        assertThat(ReferenceTransactionNumber.fromString("RFF+TN:1234").getValue()).isEqualTo(referenceTransactionNumber.getValue());
        assertThatThrownBy(() -> ReferenceTransactionNumber.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
