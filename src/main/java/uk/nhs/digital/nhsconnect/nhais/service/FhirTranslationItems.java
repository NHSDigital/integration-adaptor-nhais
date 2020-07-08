package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Parameters;

public interface FhirTranslationItems extends CommonTranslationItems {

    Parameters getParameters();

    FhirTranslationItems setParameters(Parameters parameters);
}
