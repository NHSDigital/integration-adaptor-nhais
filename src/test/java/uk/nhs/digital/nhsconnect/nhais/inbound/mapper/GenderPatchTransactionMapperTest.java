package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.GenderPatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenderPatchTransactionMapperTest {

    private static GenderPatchTransactionMapper genderPatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonSex personSex;

    @BeforeAll
    public static void setUp() {
        genderPatchTransactionMapper = new GenderPatchTransactionMapper();
    }


    @Test
    void whenTransactionHasFemaleGender_thenMapIntoFemale() {
        when(transaction.getGender()).thenReturn(Optional.of(personSex));
        when(personSex.getGender()).thenReturn(PersonSex.Gender.FEMALE);

        AmendmentPatch amendmentPatch = genderPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo("female");
    }

    @Test
    void whenTransactionHasMaleGender_thenMapIntoMale() {
        when(transaction.getGender()).thenReturn(Optional.of(personSex));
        when(personSex.getGender()).thenReturn(PersonSex.Gender.MALE);

        AmendmentPatch amendmentPatch = genderPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo("male");
    }

    @Test
    void whenTransactionHasOtherGender_thenMapIntoOther() {
        when(transaction.getGender()).thenReturn(Optional.of(personSex));
        when(personSex.getGender()).thenReturn(PersonSex.Gender.OTHER);

        AmendmentPatch amendmentPatch = genderPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo("other");
    }

    @Test
    void whenTransactionHasUnknownGender_thenMapIntoUnknown() {
        when(transaction.getGender()).thenReturn(Optional.of(personSex));
        when(personSex.getGender()).thenReturn(PersonSex.Gender.UNKNOWN);

        AmendmentPatch amendmentPatch = genderPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo("unknown");
    }

    @Test
    void whenNoGenderInTransaction_thenReturnNull() {
        when(transaction.getGender()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = genderPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}