package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NhsNumberPatchTransactionMapperTest {

    private static final String NHS_NUMBER = "1234";

    private static NhsNumberPatchTransactionMapper nhsNumberPatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonPreviousName personPreviousName;

    @BeforeAll
    public static void setUp() {
        nhsNumberPatchTransactionMapper = new NhsNumberPatchTransactionMapper();
    }


    @Test
    void whenNhsNumberPresent_thenMapIntoNhsNumberAmendmentPatch() {
        when(transaction.getPersonPreviousName()).thenReturn(Optional.of(personPreviousName));
        when(personPreviousName.getNhsNumber()).thenReturn(NHS_NUMBER);

        AmendmentPatch amendmentPatch = nhsNumberPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(NHS_NUMBER);
    }

    @Test
    void whenNhsNumberNotPresentNotPresent_thenReturnNull() {
        when(transaction.getPersonPreviousName()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = nhsNumberPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

}