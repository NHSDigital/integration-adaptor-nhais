package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper.AddressPostCodePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressPostCodePatchTransactionMapperTest {

    private static final String POST_CODE = "BR5 4ER";

    private static AddressPostCodePatchTransactionMapper addressPostCodePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonAddress personAddress;

    @BeforeAll
    public static void setUp() {
        addressPostCodePatchTransactionMapper = new AddressPostCodePatchTransactionMapper();
    }


    @Test
    void whenPersonAddressHasLineFifth_thenMapIntoCountyAmendmentPatch() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getPostalCode()).thenReturn(POST_CODE);

        AmendmentPatch amendmentPatch = addressPostCodePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(POST_CODE);
    }

    @Test
    void whenPersonAddressNotPresent_thenReturnNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = addressPostCodePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonAddressHasNoLineFifth_thenAmendmentPatchIsNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getPostalCode()).thenReturn(null);

        AmendmentPatch amendmentPatch = addressPostCodePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }
}