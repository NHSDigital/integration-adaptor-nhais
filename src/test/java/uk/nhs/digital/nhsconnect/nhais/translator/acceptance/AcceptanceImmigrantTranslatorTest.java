package uk.nhs.digital.nhsconnect.nhais.translator.acceptance;

import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceCodeMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceDateMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.AcceptanceTypeMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.DrugsMarkerMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.FreeTextMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.GpNameAndAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PartyQualifierMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonDateOfBirthMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonNameMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonOldAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonPlaceOfBirthMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonPreviousNameMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonSexMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.PreviousGpNameMapper;
import uk.nhs.digital.nhsconnect.nhais.mapper.ResidentialInstituteNameAndAddressMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptanceImmigrantTranslatorTest {

    @Mock
    private PartyQualifierMapper partyQualifierMapper;
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
    private AcceptanceImmigrantTranslator acceptanceImmigrantTranslator;

    @Test
    void When_MissingNhsNumberAndBirthPlace_Then_ThrowFhirValidationException() {
        Parameters parameters = new Parameters();
        when(validator.nhsNumberIsMissing(parameters)).thenReturn(true);
        when(validator.placeOfBirthIsMissing(parameters)).thenReturn(true);

        assertThatThrownBy(() -> acceptanceImmigrantTranslator.translate(parameters))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

}