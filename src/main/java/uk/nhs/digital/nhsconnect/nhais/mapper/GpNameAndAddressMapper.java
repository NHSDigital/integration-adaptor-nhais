package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;

public class GpNameAndAddressMapper implements FromFhirToEdifactMapper<GpNameAndAddress> {
    private final static String GP_CODE = "900";

    public GpNameAndAddress map(Parameters parameters) {
        return GpNameAndAddress.builder()
            .identifier(getPersonGP(parameters))
            .code(GP_CODE)
            .build();
    }

    private String getPersonGP(Parameters parameters) {
        Patient patient = getPatient(parameters);
        var reference = patient.getGeneralPractitionerFirstRep().getReference();

        return reference.split("/")[1];
    }
}
