package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonNameMapperTest {
    private final static String NHS_SYSTEM = "https://fhir.nhs.uk/Id/nhs-number";

    @Test
    void When_MappingPatientName_Then_ExpectCorrectResult() {
        Patient patient = new Patient();
        Identifier identifier = new Identifier();
        HumanName humanName = new HumanName();
        humanName.setFamily("Smith");
        identifier.setSystem(NHS_SYSTEM);
        identifier.setValue("1234567890");

        patient.setName(Collections.singletonList(humanName));
        patient.setIdentifier(List.of(identifier));

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName("patient")
            .setResource(patient);

        var personNameMapper = new PersonNameMapper();
        PersonName personName = personNameMapper.map(parameters);

        var expectedPersonName = PersonName
            .builder()
            .nhsNumber("1234567890")
            .surname("Smith")
            .build();

        assertEquals(expectedPersonName, personName);
    }

    @Test
    public void When_MappingWithoutNhs_Then_NoSuchElementExceptionIsThrown() {
        Patient patient = new Patient();
        HumanName humanName = new HumanName();
        humanName.setFamily("Smith");

        patient.setName(Collections.singletonList(humanName));

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName("patient")
            .setResource(patient);

        var personNameMapper = new PersonNameMapper();
        assertThrows(IllegalStateException.class, () -> personNameMapper.map(parameters));
    }

    @Test
    public void When_MappingWithoutSurname_Then_UnsupportedOperationExceptionIsThrown() {
        Patient patient = new Patient();
        Identifier identifier = new Identifier();
        identifier.setSystem(NHS_SYSTEM);
        identifier.setValue("1234567890");

        patient.setIdentifier(List.of(identifier));
        patient.setName(List.of());

        Parameters parameters = new Parameters();
        parameters.addParameter()
            .setName("patient")
            .setResource(patient);

        var personNameMapper = new PersonNameMapper();
        assertThrows(UnsupportedOperationException.class, () -> personNameMapper.map(parameters));
    }

    @Test
    public void When_MappingWithoutPatient_Then_NoSuchElementExceptionIsThrown() {
        Parameters parameters = new Parameters();

        var personNameMapper = new PersonNameMapper();
        assertThrows(NoSuchElementException.class, () -> personNameMapper.map(parameters));
    }
}
