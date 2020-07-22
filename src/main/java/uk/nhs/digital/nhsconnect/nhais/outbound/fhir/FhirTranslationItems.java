package uk.nhs.digital.nhsconnect.nhais.outbound.fhir;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.outbound.CommonTranslationItems;

public interface FhirTranslationItems extends CommonTranslationItems {

    Parameters getParameters();

    FhirTranslationItems setParameters(Parameters parameters);
}
