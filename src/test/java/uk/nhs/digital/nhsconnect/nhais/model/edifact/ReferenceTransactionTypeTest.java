package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTransactionTypeTest {

    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        ReferenceTransactionType referenceTransactionType =
                new ReferenceTransactionType(ReferenceTransactionType.TransactionType.AMENDMENT);

        String edifact = referenceTransactionType.toEdifact();

        assertEquals("RFF+950:G2'", edifact);
    }

    @Test
    void testFromString() {
        ReferenceTransactionType referenceTransactionType =
            new ReferenceTransactionType(ReferenceTransactionType.TransactionType.AMENDMENT);

        assertThat(ReferenceTransactionType.fromString("RFF+950:G2").getValue()).isEqualTo(referenceTransactionType.getValue());
        assertThatThrownBy(() -> ReferenceTransactionType.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
