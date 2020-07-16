package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Identifier;

@DatatypeDef(name="Identifier")
public class NhsIdentifier extends Identifier {

    public static final String SYSTEM = "https://fhir.nhs.uk/Id/nhs-number";

    public NhsIdentifier(String nhsNumber) {
        super();
        if (StringUtils.isBlank(nhsNumber)) {
            throw new FhirValidationException("Nhs number cannot be blank");
        }
        this.setSystem(SYSTEM);
        this.setValue(nhsNumber);
    }
}
