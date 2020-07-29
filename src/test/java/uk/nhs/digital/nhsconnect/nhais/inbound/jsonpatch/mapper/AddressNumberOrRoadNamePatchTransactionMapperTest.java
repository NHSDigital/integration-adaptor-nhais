package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper.AddressNumberOrRoadNamePatchTransactionMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressNumberOrRoadNamePatchTransactionMapperTest {

    private static final String ROAD_NAME = "12 LONG LANE";

    private static AddressNumberOrRoadNamePatchTransactionMapper addressNumberOrRoadNamePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private PersonAddress personAddress;

    @BeforeAll
    public static void setUp() {
        addressNumberOrRoadNamePatchTransactionMapper = new AddressNumberOrRoadNamePatchTransactionMapper();
    }


    @Test
    void whenPersonAddressHasLineSecond_thenMapIntoAddressNumberOrRoadNameAmendmentPatch() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getAddressLine2()).thenReturn(ROAD_NAME);

        AmendmentPatch amendmentPatch = addressNumberOrRoadNamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(ROAD_NAME);
    }

    @Test
    void whenPersonAddressNotPresent_thenReturnNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = addressNumberOrRoadNamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void whenPersonAddressHasNoLineSecond_thenReturnValueIsNull() {
        when(transaction.getPersonAddress()).thenReturn(Optional.of(personAddress));
        when(personAddress.getAddressLine2()).thenReturn(null);

        AmendmentPatch amendmentPatch = addressNumberOrRoadNamePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue()).isNull();
    }
}