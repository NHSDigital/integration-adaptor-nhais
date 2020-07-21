package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecondForenamePatchTransactionMapperTest {
    private static final String SECOND_FORENAME = "ANNE";

    private static SecondForenamePatchTransactionMapper secondForenamePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonName personName;

    @BeforeAll
    public static void setUp() {
        secondForenamePatchTransactionMapper = new SecondForenamePatchTransactionMapper();
    }


    @Test
    void whenPersonNameAndSecondNamePresent_thenMapIntoSecondForenameAmendmentPatch() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getSecondForename()).thenReturn(SECOND_FORENAME);

        AmendmentPatch amendmentPatch = secondForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(SECOND_FORENAME);
    }

    @Test
    void whenPersonNameNotPresent_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = secondForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonNamePresentNoSecondForename_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getSecondForename()).thenReturn(null);

        AmendmentPatch amendmentPatch = secondForenamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}