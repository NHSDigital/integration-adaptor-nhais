package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonGPPrevious;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonGPPreviousMapperTest {

    @Test
    void When_MappingGPPrevious_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName("previousGPName")
            .setValue(new StringType("Practitioner/Old-One"));

        var personGPPreviousMapper = new PersonGPPreviousMapper();
        PersonGPPrevious personGPPrevious = personGPPreviousMapper.map(parameters);

        var expectedPersonGPPrevious = PersonGPPrevious
            .builder()
            .identifier("Old-One")
            .code("900")
            .build();

        assertEquals(expectedPersonGPPrevious.toEdifact(), personGPPrevious.toEdifact());
    }

    @Test
    public void When_MappingWithoutGPPreviousParam_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personGPPreviousMapper = new PersonGPPreviousMapper();
        assertThrows(FhirValidationException.class, () -> personGPPreviousMapper.map(parameters));
    }
}
