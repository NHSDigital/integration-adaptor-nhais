package uk.nhs.digital.nhsconnect.nhais.mapper;

import com.google.common.collect.ImmutableMap;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonSex;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PersonSexMapper implements FromFhirToEdifactMapper<PersonSex> {
    // Be aware of:
    // class org.hl7.fhir.r4.model.Enumerations$AdministrativeGender
    // class org.hl7.fhir.r4.model.codesystems.AdministrativeGender
    private final static Map<Enumerations.AdministrativeGender, String> PATIENT_SEX_CODE = ImmutableMap.of(
            Enumerations.AdministrativeGender.UNKNOWN, "Not known",
            Enumerations.AdministrativeGender.MALE, "Male",
            Enumerations.AdministrativeGender.FEMALE, "Female",
            Enumerations.AdministrativeGender.OTHER, "Not specified"
    );

    public PersonSex map(Parameters parameters) {
        return PersonSex.builder()
                .sexCode(getPersonSex(parameters))
                .build();
    }

    private String getPersonSex(Parameters parameters) {
        Patient patient = parameters.getParameter()
                .stream()
                .filter(param -> Patient.class.getSimpleName().equals(param.getName()))
                .map(Parameters.ParametersParameterComponent::getResource)
                .map(Patient.class::cast)
                .findFirst()
                .orElseThrow();

        return Optional.ofNullable(PATIENT_SEX_CODE.get(patient.getGender()))
                .orElseThrow(() -> new NoSuchElementException("sex code not found: " + patient.getGender().getDisplay()));
    }
}
