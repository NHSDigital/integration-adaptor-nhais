package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.SurnamePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SurnamePatchTransactionMapperTest {

    private static final String SURNAME = "JONES";

    private static SurnamePatchTransactionMapper surnamePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonName personName;

    @BeforeAll
    public static void setUp() {
        surnamePatchTransactionMapper = new SurnamePatchTransactionMapper();
    }


    @Test
    void whenPersonNameAndSurnamePresent_thenMapIntoSurnameAmendmentPatch() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getSurname()).thenReturn(SURNAME);

        AmendmentPatch amendmentPatch = surnamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(SURNAME);
    }

    @Test
    void whenPersonNAmeNotPresent_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = surnamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonNameHasNoSurname_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getSurname()).thenReturn(null);

        AmendmentPatch amendmentPatch = surnamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}