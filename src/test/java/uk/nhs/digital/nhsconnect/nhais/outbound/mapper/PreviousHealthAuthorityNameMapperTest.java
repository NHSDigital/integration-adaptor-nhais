package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PreviousHealthAuthorityName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PreviousHealthAuthorityNameMapperTest {

    private final static String IDENTIFIER = "ID1";

    @Test
    void When_MappingGPPrevious_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.PREVIOUS_HA_CIPHER)
            .setValue(new StringType(IDENTIFIER));

        var previousHealthAuthorityNameMapper = new PreviousHealthAuthorityNameMapper();
        var previousHealthAuthorityName = previousHealthAuthorityNameMapper.map(parameters);

        var expectedPreviousHealthAuthorityName = new PreviousHealthAuthorityName(IDENTIFIER);

        assertThat(previousHealthAuthorityName.toEdifact()).isEqualTo(expectedPreviousHealthAuthorityName.toEdifact());
    }

    @Test
    public void When_MappingWithoutGPPreviousParam_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personGPPreviousMapper = new PreviousGpNameMapper();
        assertThatThrownBy(() -> personGPPreviousMapper.map(parameters))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

}