package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;

@DatatypeDef(name="Extension")
public class BirthPlaceExtension extends Extension {
    public static final String URL = "http://hl7.org/fhir/StructureDefinition/patient-birthPlace";

    public BirthPlaceExtension(String value) {
        super(URL, new StringType(value));
    }
}
