package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

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
        var abbreviationMap = ImmutableMap.<String, ReferenceTransactionType.TransactionType>builder()
            .put("AMF", ReferenceTransactionType.Inbound.AMENDMENT)
            .put("DEF", ReferenceTransactionType.Inbound.DEDUCTION)
            .put("REF", ReferenceTransactionType.Inbound.REJECTION)
            .put("APF", ReferenceTransactionType.Inbound.APPROVAL)
            .put("DRR", ReferenceTransactionType.Inbound.DEDUCTION_REJECTION)
            .put("FFR", ReferenceTransactionType.Inbound.FP69_FLAG_REMOVAL)
            .put("FPN", ReferenceTransactionType.Inbound.FP69_PRIOR_NOTIFICATION)
            .put("ACG", ReferenceTransactionType.Outbound.ACCEPTANCE)
            .put("AMG", ReferenceTransactionType.Outbound.AMENDMENT)
            .put("REG", ReferenceTransactionType.Outbound.REMOVAL)
            .put("DER", ReferenceTransactionType.Outbound.DEDUCTION)
            .build();

        assertThat(abbreviationMap).hasSize(
            ReferenceTransactionType.Inbound.values().length +
                ReferenceTransactionType.Outbound.values().length);

        abbreviationMap.forEach((abbreviation, transactionType) ->
            assertThat(ReferenceTransactionType.TransactionType.fromAbbreviation(abbreviation)).isEqualTo(transactionType));
    }

    @Test
    void testFromCode() {
        var codeMap = ImmutableMap.<String, ReferenceTransactionType.TransactionType>builder()
            .put("F1", ReferenceTransactionType.Inbound.AMENDMENT)
            .put("F2", ReferenceTransactionType.Inbound.DEDUCTION)
            .put("F3", ReferenceTransactionType.Inbound.REJECTION)
            .put("F4", ReferenceTransactionType.Inbound.APPROVAL)
            .put("F9", ReferenceTransactionType.Inbound.FP69_PRIOR_NOTIFICATION)
            .put("F10", ReferenceTransactionType.Inbound.FP69_FLAG_REMOVAL)
            .put("F11", ReferenceTransactionType.Inbound.DEDUCTION_REJECTION)
            .put("G1", ReferenceTransactionType.Outbound.ACCEPTANCE)
            .put("G2", ReferenceTransactionType.Outbound.AMENDMENT)
            .put("G3", ReferenceTransactionType.Outbound.REMOVAL)
            .put("G5", ReferenceTransactionType.Outbound.DEDUCTION)
            .build();

        assertThat(codeMap).hasSize(
            ReferenceTransactionType.Inbound.values().length +
                ReferenceTransactionType.Outbound.values().length);

        codeMap.forEach((code, transactionType) ->
            assertThat(ReferenceTransactionType.TransactionType.fromCode(code)).isEqualTo(transactionType));
    }
}
