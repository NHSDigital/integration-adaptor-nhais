package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import uk.nhs.digital.nhsconnect.nhais.outbound.ParameterValidationException;
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
    public void When_AcceptanceRequestParameter_Expect_ReturnAcceptanceType() {
        String parameter = "$nhais.acceptance";
        assertThat(transactionTypeMapper.mapTransactionType(parameter))
            .isEqualTo(ReferenceTransactionType.Outbound.ACCEPTANCE);
    }

    @Test
    public void When_RemovalRequestParameter_Expect_ReturnAcceptanceType() {
        String parameter = "$nhais.removal";
        assertThat(transactionTypeMapper.mapTransactionType(parameter))
            .isEqualTo(ReferenceTransactionType.Outbound.REMOVAL);
    }

    @Test
    public void When_DeductionRequestParameter_Expect_ReturnAcceptanceType() {
        String parameter = "$nhais.deduction";
        assertThat(transactionTypeMapper.mapTransactionType(parameter))
            .isEqualTo(ReferenceTransactionType.Outbound.DEDUCTION);
    }

    @Test
    public void When_UnknownRequestParameter_Expect_ReturnParameterValidationException() {
        String parameter = "$nhais.addPatient";
        assertThatThrownBy(() -> transactionTypeMapper.mapTransactionType(parameter))
            .isExactlyInstanceOf(ParameterValidationException.class);
    }
}
