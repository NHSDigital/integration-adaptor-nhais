package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import org.hl7.fhir.r4.model.Identifier;

@DatatypeDef(name = "Identifier")
public class GeneralPractitionerIdentifier extends Identifier {
    private static final String SYSTEM = "https://fhir.hl7.org.uk/Id/gmc-number";

    public GeneralPractitionerIdentifier(String gpId) {
        super();
        this.setSystem(SYSTEM);
        this.setValue(gpId);
    }
}