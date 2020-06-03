package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;

public class PersonNameMapper implements FromFhirToEdifactMapper<PersonName> {
    private final static String PATIENT_PARAM = "patient";

    public PersonName map(Parameters parameters) {

        Patient patient = getPatient(parameters);

        return PersonName.builder()
                .surname(patient.getNameFirstRep().getFamily())
                .build();
    }

    private Patient getPatient(Parameters parameters) {
        return parameters.getParameter()
                .stream()
                .filter(param -> PATIENT_PARAM.equals(param.getName()))
                .map(Parameters.ParametersParameterComponent::getResource)
                .map(Patient.class::cast)
                .findFirst()
                .orElseThrow();
    }

}
