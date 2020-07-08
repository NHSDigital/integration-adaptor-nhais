package uk.nhs.digital.nhsconnect.nhais.translator;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;

import java.util.List;

public interface AmendmentToEdifactTranslator {

    List<Segment> translate(AmendmentBody amendmentBody) throws FhirValidationException;
}
