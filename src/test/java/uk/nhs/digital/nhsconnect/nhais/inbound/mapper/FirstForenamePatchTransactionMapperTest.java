package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.FirstForenamePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FirstForenamePatchTransactionMapperTest {

    private static final String FIRST_FORENAME = "SALLY";

    private static FirstForenamePatchTransactionMapper firstForenamePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonName personName;

    @BeforeAll
    public static void setUp() {
        firstForenamePatchTransactionMapper = new FirstForenamePatchTransactionMapper();
    }


    @Test
    void whenPersonNameHasFirstForename_thenMapIntoFirstForenameAmendmentPatch() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getFirstForename()).thenReturn(FIRST_FORENAME);

        AmendmentPatch amendmentPatch = firstForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(FIRST_FORENAME);
    }

    @Test
    void whenPersonNameNotPresent_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = firstForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonNameHasNoForename_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getFirstForename()).thenReturn(null);

        AmendmentPatch amendmentPatch = firstForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}