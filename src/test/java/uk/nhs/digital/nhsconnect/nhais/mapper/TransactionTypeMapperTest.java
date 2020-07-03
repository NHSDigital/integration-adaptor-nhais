package uk.nhs.digital.nhsconnect.nhais.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import uk.nhs.digital.nhsconnect.nhais.exceptions.ParameterValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class TransactionTypeMapperTest {

    private static TransactionTypeMapper transactionTypeMapper;

    @BeforeAll
    public static void setUp() {
        transactionTypeMapper = new TransactionTypeMapper();
    }

    @Test
    public void whenAcceptanceRequestParameter_thenReturnAcceptanceType() {
        String parameter = "$nhais.acceptance";
        assertThat(transactionTypeMapper.mapTransactionType(parameter))
            .isEqualTo(ReferenceTransactionType.Outbound.ACCEPTANCE);
    }

    @Test
    public void whenRemovalRequestParameter_thenReturnAcceptanceType() {
        String parameter = "$nhais.removal";
        assertThat(transactionTypeMapper.mapTransactionType(parameter))
            .isEqualTo(ReferenceTransactionType.Outbound.REMOVAL);
    }

    @Test
    public void whenDeductionRequestParameter_thenReturnAcceptanceType() {
        String parameter = "$nhais.deduction";
        assertThat(transactionTypeMapper.mapTransactionType(parameter))
            .isEqualTo(ReferenceTransactionType.Outbound.DEDUCTION);
    }

    @Test
    public void whenUnknownRequestParameter_thenReturnParameterValidationException() {
        String parameter = "$nhais.addPatient";
        assertThatThrownBy(() -> transactionTypeMapper.mapTransactionType(parameter))
            .isExactlyInstanceOf(ParameterValidationException.class);

    }
}
