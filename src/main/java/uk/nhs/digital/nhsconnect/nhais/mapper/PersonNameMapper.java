package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PersonNameMapper implements FromFhirToEdifactMapper<PersonName> {
    private final static String NHS_SYSTEM = "https://fhir.nhs.uk/Id/nhs-number";

    public PersonName map(Parameters parameters) {

        Patient patient = ParametersExtension.extractPatient(parameters);

        return PersonName.builder()
            .nhsNumber(getNhsNumber(patient))
            .patientIdentificationType(PersonName.PatientIdentificationType.OPI)
            .familyName(patient.getNameFirstRep().getFamily())
            .build();
    }

    private String getNhsNumber(Patient patient) {
        return patient.getIdentifier().stream()
            .filter(identifier -> identifier.getSystem().equals(NHS_SYSTEM))
            .map(Identifier::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nhs Number mapping problem"));
    }
}
