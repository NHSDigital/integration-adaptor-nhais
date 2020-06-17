package uk.nhs.digital.nhsconnect.nhais.translator;

import org.hl7.fhir.r4.model.Parameters;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.List;

/**
 * A "translator" converts an entire FHIR message to many EDIFACT segments. The translator may use separate "mapper"
 * classes to map FHIR elements onto single EDIFACT segments.
 */
public interface FhirToEdifactTranslator {

    List<Segment> translate(Parameters parameters) throws FhirValidationException;

}
