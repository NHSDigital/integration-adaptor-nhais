package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.AcceptanceType;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcceptanceTypeMapperTest {

    @Test
    void When_MappingAcceptanceType_Then_ExpectCorrectResult() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.ACCEPTANCE_TYPE)
            .setValue(new StringType(AcceptanceType.AvailableTypes.BIRTH.getValue()));

        var acceptanceTypeMapper = new AcceptanceTypeMapper();
        AcceptanceType acceptanceType = acceptanceTypeMapper.map(parameters);

        var exceptedAcceptanceType = AcceptanceType.builder()
            .type("1")
            .build();

        assertEquals(exceptedAcceptanceType, acceptanceType);
    }

    @Test
    public void When_MappingWithWrongType_Then_NoSuchElementExceptionIsThrown() {
        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName(ParameterNames.ACCEPTANCE_TYPE)
            .setValue(new StringType("test-fails"));

        var acceptanceTypeMapper = new AcceptanceTypeMapper();
        assertThrows(NoSuchElementException.class, () -> acceptanceTypeMapper.map(parameters));
    }

    @Test
    public void When_MappingWithoutTypeParam_Then_FhirValidationExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var acceptanceTypeMapper = new AcceptanceTypeMapper();
        assertThrows(FhirValidationException.class, () -> acceptanceTypeMapper.map(parameters));
    }
}
