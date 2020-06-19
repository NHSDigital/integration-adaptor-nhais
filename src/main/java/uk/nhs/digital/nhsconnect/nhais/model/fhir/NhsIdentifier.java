package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;

import org.hl7.fhir.r4.model.Identifier;

@DatatypeDef(name="Identifier")
public class NhsIdentifier extends Identifier {

    public static final String SYSTEM = "https://fhir.nhs.uk/Id/nhs-number";

    public NhsIdentifier(String nhsNumber) {
        super();
        this.setSystem(SYSTEM);
        this.setValue(nhsNumber);
    }
}
