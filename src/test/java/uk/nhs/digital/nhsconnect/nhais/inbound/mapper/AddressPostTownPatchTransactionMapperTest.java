package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.AddressPostTownPatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressPostTownPatchTransactionMapperTest {

    private static final String POST_TOWN = "BROMLEY";

    private static AddressPostTownPatchTransactionMapper addressPostTownPatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonAddress personAddress;

    @BeforeAll
    public static void setUp() {
        addressPostTownPatchTransactionMapper = new AddressPostTownPatchTransactionMapper();
    }


    @Test
    void whenPersonAddressHasLineForth_thenMapIntoCountyAmendmentPatch() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getAddressLine4()).thenReturn(POST_TOWN);

        AmendmentPatch amendmentPatch = addressPostTownPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(POST_TOWN);
    }

    @Test
    void whenPersonAddressNotPresent_thenReturnNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = addressPostTownPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonAddressHasNoLineForth_thenReturnValueIsNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getAddressLine4()).thenReturn(null);

        AmendmentPatch amendmentPatch = addressPostTownPatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue()).isNull();
    }
}