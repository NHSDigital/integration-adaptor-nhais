package uk.nhs.digital.nhsconnect.nhais.outbound.translator.acceptance;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.AcceptanceCodeMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.AcceptanceDateMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.AcceptanceTypeMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.DrugsMarkerMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonDateOfBirthMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonOldAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonPlaceOfBirthMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonPreviousNameMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PersonSexMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.PreviousGpNameMapper;
import uk.nhs.digital.nhsconnect.nhais.outbound.mapper.ResidentialInstituteNameAndAddressMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptanceTransferInTranslatorTest {

    @Mock private PartyQualifierMapper partyQualifierMapper;
    @Mock private GpNameAndAddressMapper gpNameAndAddressMapper;
    @Mock private AcceptanceCodeMapper acceptanceCodeMapper;
    @Mock private AcceptanceTypeMapper acceptanceTypeMapper;
    @Mock private AcceptanceDateMapper acceptanceDateMapper;
    @Mock private PersonNameMapper personNameMapper;
    @Mock private PersonPlaceOfBirthMapper personPlaceOfBirthMapper;
    @Mock private PersonPreviousNameMapper personPreviousNameMapper;
    @Mock private PersonSexMapper personSexMapper;
    @Mock private PersonAddressMapper personAddressMapper;
    @Mock private PersonDateOfBirthMapper personDateOfBirthMapper;
    @Mock private DrugsMarkerMapper drugsMarkerMapper;
    @Mock private FreeTextMapper freeTextMapper;
    @Mock private ResidentialInstituteNameAndAddressMapper residentialInstituteNameAndAddressMapper;
    @Mock private PersonOldAddressMapper personOldAddressMapper;
    @Mock private PreviousGpNameMapper previousGpNameMapper;
    @Mock private OptionalInputValidator validator;

    @InjectMocks
    private AcceptanceTransferInTranslator acceptanceTransferInTranslator;

    @Test
    void When_MissingNhsNumberAndBirthPlace_Then_ThrowFhirValidationException() {
        Parameters parameters = new Parameters();
        when(validator.nhsNumberIsMissing(parameters)).thenReturn(true);
        when(validator.placeOfBirthIsMissing(parameters)).thenReturn(true);

        assertThatThrownBy(() -> acceptanceTransferInTranslator.translate(parameters))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

}