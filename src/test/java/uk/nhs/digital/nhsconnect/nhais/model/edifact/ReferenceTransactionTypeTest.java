package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;

public class ReferenceTransactionTypeTest {

    @Test
    public void test() {
        ReferenceTransactionType referenceTransactionType =
                new ReferenceTransactionType(ReferenceTransactionType.TransactionType.AMENDMENT);
    }
}
