package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTransactionNumberTest {
    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        ReferenceTransactionNumber referenceTransactionNumber =
                new ReferenceTransactionNumber(1234);

        String edifact = referenceTransactionNumber.toEdifact();

        assertEquals("RFF+TN:1234'", edifact);
    }
}
