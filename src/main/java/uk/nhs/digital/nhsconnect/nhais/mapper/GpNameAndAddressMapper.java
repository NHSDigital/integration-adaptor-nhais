package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;

public class GpNameAndAddressMapper implements FromFhirToEdifactMapper<GpNameAndAddress> {

    public GpNameAndAddress map(Parameters parameters) {
        return GpNameAndAddress.builder()
            .identifier(getPersonGP(parameters))
            .code(getPersonGP(parameters))
            .build();
    }

    private String getPersonGP(Parameters parameters) {
        Patient patient = getPatient(parameters);
        var reference = patient.getGeneralPractitionerFirstRep().getReference();

        return reference.split("/")[1];
    }
}
