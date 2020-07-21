package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.TitlePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TitlePatchTransactionMapperTest {

    private static final String TITLE = "MRS";

    private static TitlePatchTransactionMapper titlePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonName personName;

    @BeforeAll
    public static void setUp() {
        titlePatchTransactionMapper = new TitlePatchTransactionMapper();
    }


    @Test
    void whenPersonNameAndTitlePresent_thenMapIntoTitleAmendmentPatch() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getTitle()).thenReturn(TITLE);

        AmendmentPatch amendmentPatch = titlePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(TITLE);
    }

    @Test
    void whenPersonNameNotPresent_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = titlePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonNameHasNoTitle_thenReturnNull() {
        when(transaction.getPersonName()).thenReturn(Optional.of(personName));
        when(personName.getTitle()).thenReturn(null);

        AmendmentPatch amendmentPatch = titlePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}