package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressLocalityPatchTransactionMapperTest {

    private static final String LOCALITY = "LITTLE HAMLET";

    private static AddressLocalityPatchTransactionMapper addressLocalityPatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonAddress personAddress;

    @BeforeAll
    public static void setUp() {
        addressLocalityPatchTransactionMapper = new AddressLocalityPatchTransactionMapper();
    }


    @Test
    void whenPersonAddressHasLineThird_thenMapIntoLocalityAmendmentPatch() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getAddressLine3()).thenReturn(LOCALITY);

        AmendmentPatch amendmentPatch = addressLocalityPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(LOCALITY);
    }

    @Test
    void whenPersonAddressNotPresent_thenReturnNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = addressLocalityPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonAddressHasNoLineThird_thenReturnValueIsNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getAddressLine3()).thenReturn(null);

        AmendmentPatch amendmentPatch = addressLocalityPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue()).isNull();
    }
}