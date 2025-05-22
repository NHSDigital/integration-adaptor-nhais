package uk.nhs.digital.nhsconnect.nhais.inbound.jsonpatch.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ResidentialInstituteNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch.REMOVE_INDICATOR;

@ExtendWith(MockitoExtension.class)
class ResidentialInstituteCodePatchTransactionMapperTest {

    private static final String IDENTIFIER = "CODE";

    private static ResidentialInstituteCodePatchTransactionMapper residentialInstituteCodePatchTransactionMapper;

    @Mock
    private Transaction transaction;

    @Mock
    private ResidentialInstituteNameAndAddress residentialInstituteNameAndAddress;

    @BeforeAll
    public static void setUp() {
        residentialInstituteCodePatchTransactionMapper = new ResidentialInstituteCodePatchTransactionMapper();
    }


    @Test
    void When_ResidentialInstitutionPresent_Expect_MapIntoResidentialInstitutionCodeAmendmentPatch() {
        when(transaction.getResidentialInstitution()).thenReturn(Optional.of(residentialInstituteNameAndAddress));
        when(residentialInstituteNameAndAddress.getIdentifier()).thenReturn(IDENTIFIER);

        AmendmentPatch amendmentPatch = residentialInstituteCodePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isEqualTo(IDENTIFIER);
    }

    @Test
    void When_ResidentialInstituteNotPresent_Expect_ReturnNull() {
        when(transaction.getResidentialInstitution()).thenReturn(Optional.empty());

        AmendmentPatch amendmentPatch = residentialInstituteCodePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch).isNull();
    }

    @Test
    void When_ResidentialInstituteCodeIsRemoveIndication_Expect_ReturnValueIsNull() {
        when(transaction.getResidentialInstitution()).thenReturn(Optional.of(residentialInstituteNameAndAddress));
        when(residentialInstituteNameAndAddress.getIdentifier()).thenReturn(REMOVE_INDICATOR);

        AmendmentPatch amendmentPatch = residentialInstituteCodePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isNull();
    }

    @Test
    void When_ResidentialInstituteCodeIsNull_Expect_ReturnValueIsNull() {
        when(transaction.getResidentialInstitution()).thenReturn(Optional.of(residentialInstituteNameAndAddress));
        when(residentialInstituteNameAndAddress.getIdentifier()).thenReturn(null);

        AmendmentPatch amendmentPatch = residentialInstituteCodePatchTransactionMapper.map(transaction);

        assertThat(amendmentPatch.getAmendmentValue().get()).isNull();
    }
}