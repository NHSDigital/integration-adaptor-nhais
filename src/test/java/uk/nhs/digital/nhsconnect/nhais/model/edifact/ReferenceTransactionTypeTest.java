package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTransactionTypeTest {

    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        ReferenceTransactionType referenceTransactionType =
                new ReferenceTransactionType(ReferenceTransactionType.TransactionType.AMENDMENT);

        String edifact = referenceTransactionType.toEdifact();

        assertEquals("RFF+950:G2'", edifact);
    }
}
