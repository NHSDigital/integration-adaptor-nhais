package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTransactionTypeTest {

    @Test
    public void testValidReferenceTransactionType() throws EdifactValidationException {
        ReferenceTransactionType referenceTransactionType =
            new ReferenceTransactionType(ReferenceTransactionType.Outbound.AMENDMENT);

        String edifact = referenceTransactionType.toEdifact();

        assertEquals("RFF+950:G2'", edifact);
    }

    @Test
    void testFromString() {
        ReferenceTransactionType referenceTransactionType =
            new ReferenceTransactionType(ReferenceTransactionType.Outbound.AMENDMENT);

        assertThat(ReferenceTransactionType.fromString("RFF+950:G2").getValue()).isEqualTo(referenceTransactionType.getValue());
        assertThatThrownBy(() -> ReferenceTransactionType.fromString("wrong value")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testFromAbbreviation() {
        var abbreviationMap = Map.of(
            "AMF", ReferenceTransactionType.Inbound.AMENDMENT,
            "DEF", ReferenceTransactionType.Inbound.DEDUCTION,
            "REF", ReferenceTransactionType.Inbound.REJECTION,
            "APF", ReferenceTransactionType.Inbound.APPROVAL,
            "ACG", ReferenceTransactionType.Outbound.ACCEPTANCE,
            "AMG", ReferenceTransactionType.Outbound.AMENDMENT,
            "REG", ReferenceTransactionType.Outbound.REMOVAL,
            "DER", ReferenceTransactionType.Outbound.DEDUCTION
        );

        assertThat(abbreviationMap).hasSize(
            ReferenceTransactionType.Inbound.values().length +
                ReferenceTransactionType.Outbound.values().length);

        abbreviationMap.forEach((abbreviation, transactionType) ->
            assertThat(ReferenceTransactionType.TransactionType.fromAbbreviation(abbreviation)).isEqualTo(transactionType));
    }
}
