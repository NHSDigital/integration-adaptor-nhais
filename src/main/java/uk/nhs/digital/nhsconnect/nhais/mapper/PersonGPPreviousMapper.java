package uk.nhs.digital.nhsconnect.nhais.mapper;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonGPPrevious;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.util.Objects;

public class PersonGPPreviousMapper implements FromFhirToEdifactMapper<PersonGPPrevious> {
    private final static String GP_CODE = "900";
    private final static String PREVIOUS_GP_PARAM = "previousGPName";

    public PersonGPPrevious map(Parameters parameters) {
        return PersonGPPrevious.builder()
            .identifier(getPersonPreviousGP(parameters))
            .code(GP_CODE)
            .build();
    }

    private String getPersonPreviousGP(Parameters parameters) {
        ParametersExtension parametersExt = new ParametersExtension(parameters);
        return splitPractitionerString(
            parametersExt.extractValueOrThrow(PREVIOUS_GP_PARAM, () -> new FhirValidationException("Error while parsing param: " + PREVIOUS_GP_PARAM))
        );
    }

    private String splitPractitionerString(String value) {
        return value.split("/")[1];
    }
}
