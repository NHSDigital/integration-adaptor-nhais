package uk.nhs.digital.nhsconnect.nhais.outbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PreviousGpName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PreviousGpNameMapperTest {

    @Test
    void When_MappingGPPrevious_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.PREVIOUS_GP_NAME)
            .setValue(new StringType("DR PREVIOUS"));

        var personGPPreviousMapper = new PreviousGpNameMapper();
        PreviousGpName previousGpName = personGPPreviousMapper.map(parameters);

        var expectedPersonGPPrevious = PreviousGpName
            .builder()
            .partyName("DR PREVIOUS")
            .build();

        assertEquals(expectedPersonGPPrevious.toEdifact(), previousGpName.toEdifact());
    }

    @Test
    public void When_MappingWithoutGPPreviousParam_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personGPPreviousMapper = new PreviousGpNameMapper();
        assertThrows(FhirValidationException.class, () -> personGPPreviousMapper.map(parameters));
    }
}
