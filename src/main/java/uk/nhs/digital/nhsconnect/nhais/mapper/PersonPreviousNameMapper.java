package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PatientIdentificationType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonPreviousName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PersonPreviousNameMapper implements OptionalFromFhirToEdifactMapper<PersonPreviousName> {

    public PersonPreviousName map(Parameters parameters) {

        Patient patient = ParametersExtension.extractPatient(parameters);

        HumanName previousName = patient.getName()
            .stream()
            .limit(2)
            .skip(1)
            .findFirst()
            .orElseThrow(() -> new FhirValidationException("Previous name is not defined in request params"));

        return PersonPreviousName.builder()
            .nhsNumber(getNhsNumber(patient))
            .patientIdentificationType(PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION) //TODO: handle AMENDED_PATIENT_IDENTIFICATION
            .familyName(previousName.getFamily())
            .build();
    }

    private String getNhsNumber(Patient patient) {
        return patient.getIdentifier().stream()
            .filter(identifier -> identifier.getSystem().equals(NhsIdentifier.SYSTEM))
            .map(Identifier::getValue)
            .findFirst()
            .orElse(null);
    }

    @Override
    public boolean canMap(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        patient.getName();
        return patient.getName().size() > 1;
    }
}
