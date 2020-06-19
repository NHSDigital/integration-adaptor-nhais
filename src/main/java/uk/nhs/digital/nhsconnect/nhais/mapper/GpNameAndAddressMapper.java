package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.GpNameAndAddress;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class GpNameAndAddressMapper implements FromFhirToEdifactMapper<GpNameAndAddress> {
    private final static String GP_CODE = "900";

    public GpNameAndAddress map(Parameters parameters) {
        return GpNameAndAddress.builder()
            .identifier(getPersonGP(parameters))
            .code(GP_CODE)
            .build();
    }

    private String getPersonGP(Parameters parameters) {
        Patient patient = ParametersExtension.extractPatient(parameters);
        return patient.getGeneralPractitionerFirstRep().getIdentifier().getValue();
    }
}
