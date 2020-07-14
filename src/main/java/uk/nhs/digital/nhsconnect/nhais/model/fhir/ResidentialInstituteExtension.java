package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import lombok.NonNull;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;

@DatatypeDef(name="Extension")
public class ResidentialInstituteExtension extends Extension {
    public static final String URL = "https://fhir.nhs.uk/R4/StructureDefinition/Extension-UKCore-NHAIS-ResidentialInstituteCode";

    public ResidentialInstituteExtension(@NonNull String value) {
        super(URL, new StringType(value));
    }

}
