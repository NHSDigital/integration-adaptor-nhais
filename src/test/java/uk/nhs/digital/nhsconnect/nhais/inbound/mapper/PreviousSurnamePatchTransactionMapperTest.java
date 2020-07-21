package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.PreviousSurnamePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreviousSurnamePatchTransactionMapperTest {

    private static final String PREVIOUS_SURNAME = "ROBINSON";

    private static PreviousSurnamePatchTransactionMapper previousSurnamePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonPreviousName personPreviousName;

    @BeforeAll
    public static void setUp() {
        previousSurnamePatchTransactionMapper = new PreviousSurnamePatchTransactionMapper();
    }


    @Test
    void whenPreviousNameHasSurname_thenMapIntoPreviousSurnameAmendmentPatch() {
        when(transaction.getPersonPreviousName()).thenReturn(Optional.of(personPreviousName));
        when(personPreviousName.getFamilyName()).thenReturn(PREVIOUS_SURNAME);

        AmendmentPatch amendmentPatch = previousSurnamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(PREVIOUS_SURNAME);
    }

    @Test
    void whenNoPreviousName_thenReturnNull() {
        when(transaction.getPersonPreviousName()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = previousSurnamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPreviousNameHasNoSurname_thenAmendmentIsNull() {
        when(transaction.getPersonPreviousName()).thenReturn(Optional.of(personPreviousName));
        when(personPreviousName.getFamilyName()).thenReturn(null);

        AmendmentPatch amendmentPatch = previousSurnamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}