package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
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
            .practitioner("Old-One")
            .build();

        assertEquals(expectedPersonGPPrevious, personGPPrevious);

    }

    @Test
    public void When_MappingWithoutGPPrevious_Then_NoSuchElementExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personGPPreviousMapper = new PersonGPPreviousMapper();
        assertThrows(IllegalStateException.class, () -> personGPPreviousMapper.map(parameters));
    }
}
