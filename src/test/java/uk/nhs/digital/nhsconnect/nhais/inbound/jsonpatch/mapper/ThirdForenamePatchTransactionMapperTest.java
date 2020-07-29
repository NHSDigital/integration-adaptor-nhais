package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper.ThirdForenamePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ThirdForenamePatchTransactionMapperTest {

    private static final String THIRD_NAME = "MARY";

    private static ThirdForenamePatchTransactionMapper thirdForenamePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonName personName;

    @BeforeAll
    public static void setUp() {
        thirdForenamePatchTransactionMapper = new ThirdForenamePatchTransactionMapper();
    }


    @Test
    void whenPersonNameAndOtherForenamePresent_thenMapIntoThirdForenameAmendmentPatch() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getOtherForenames()).thenReturn(THIRD_NAME);

        AmendmentPatch amendmentPatch = thirdForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(THIRD_NAME);
    }

    @Test
    void whenPersonNamesNotPresent_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = thirdForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonNameHasNoOtherForename_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getOtherForenames()).thenReturn(null);

        AmendmentPatch amendmentPatch = thirdForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}