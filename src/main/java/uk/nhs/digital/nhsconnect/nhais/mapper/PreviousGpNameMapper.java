package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PreviousGpName;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

@Component
public class PreviousGpNameMapper implements FromFhirToEdifactMapper<PreviousGpName> {
    private final static String PREVIOUS_GP_PARAM = "previousGPName";

    public PreviousGpName map(Parameters parameters) {
        return PreviousGpName.builder()
            .partyName(getPersonPreviousGP(parameters))
            .build();
    }

    private String getPersonPreviousGP(Parameters parameters) {
        ParametersExtension parametersExt = new ParametersExtension(parameters);
        return parametersExt.extractValue(PREVIOUS_GP_PARAM);
    }

}
