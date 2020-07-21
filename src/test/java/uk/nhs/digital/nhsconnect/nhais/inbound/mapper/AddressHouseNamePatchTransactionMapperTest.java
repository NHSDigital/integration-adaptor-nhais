package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.mapper.AddressHouseNamePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressHouseNamePatchTransactionMapperTest {

    private static final String HOUSE_NAME = "HOLLY COTTAGE";

    private static AddressHouseNamePatchTransactionMapper addressHouseNamePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonAddress personAddress;

    @BeforeAll
    public static void setUp() {
        addressHouseNamePatchTransactionMapper = new AddressHouseNamePatchTransactionMapper();
    }

    @Test
    void whenPersonAddressHasLineFirst_theMapIntoHouseNameAmendmentPatch() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getAddressLine1()).thenReturn(HOUSE_NAME);

        AmendmentPatch amendmentPatch = addressHouseNamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(HOUSE_NAME);
    }

    @Test
    void whenPersonAddressNotPresent_thenReturnNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = addressHouseNamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonAddressHasNoLineFirst_thenReturnValueIsNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getAddressLine1()).thenReturn(null);

        AmendmentPatch amendmentPatch = addressHouseNamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue()).isNull();
    }
}