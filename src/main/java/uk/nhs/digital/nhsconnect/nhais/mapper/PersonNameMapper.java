package uk.nhs.digital.nhsconnect.nhais.mapper;

import java.util.stream.Collectors;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;

@Component
public class PersonNameMapper implements FromFhirToEdifactMapper<PersonName> {

    public PersonName map(Parameters parameters) {

        Patient patient = ParametersExtension.extractPatient(parameters);

        HumanName nameFirstRep = patient.getNameFirstRep();

        return PersonName.builder()
            .nhsNumber(getNhsNumber(patient))
            .patientIdentificationType(PersonName.PatientIdentificationType.OFFICIAL_PATIENT_IDENTIFICATION) //TODO: handle AMENDED_PATIENT_IDENTIFICATION
            .familyName(nameFirstRep.getFamily())
            .forename(nameFirstRep.getGiven().stream()
                .findFirst()
                .map(StringType::toString)
                .orElse(null))
            .title(StringUtils.stripToNull(nameFirstRep.getPrefixAsSingleString()))
            .middleName(nameFirstRep.getGiven().stream()
                .skip(1)
                .findFirst()
                .map(StringType::toString)
                .orElse(null))
            .thirdForename(StringUtils.stripToNull(
                nameFirstRep.getGiven().stream()
                    .skip(2)
                    .map(StringType::toString)
                    .collect(Collectors.joining(" "))
                )
            )
            .build();
    }

    private String getNhsNumber(Patient patient) {
        return patient.getIdentifier().stream()
            .filter(identifier -> identifier.getSystem().equals(NhsIdentifier.SYSTEM))
            .map(Identifier::getValue)
            .findFirst()
            .orElse(null);
    }

}
