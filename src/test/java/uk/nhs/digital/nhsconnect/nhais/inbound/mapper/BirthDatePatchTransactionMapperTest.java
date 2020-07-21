package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.BirthDatePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BirthDatePatchTransactionMapperTest {

    private static final String BIRTH_DATE_STRING = "1980-05-23";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1980, 05, 23);

    private static BirthDatePatchTransactionMapper birthDatePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonDateOfBirth personDateOfBirth;

    @BeforeAll
    public static void setUp() {
        birthDatePatchTransactionMapper = new BirthDatePatchTransactionMapper();
    }


    @Test
    void whenPersonDateOfBirth_thenMapIntoBirthDateAmendmentPatch() {
        when(transaction.getPersonDateOfBirth()).thenReturn(Optional.of(personDateOfBirth));
        when(personDateOfBirth.getDateOfBirth()).thenReturn(BIRTH_DATE);

        AmendmentPatch amendmentPatch = birthDatePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(BIRTH_DATE_STRING);
    }

    @Test
    void whenPersonAddressNotPresent_thenReturnNull() {
        when(transaction.getPersonDateOfBirth()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = birthDatePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}