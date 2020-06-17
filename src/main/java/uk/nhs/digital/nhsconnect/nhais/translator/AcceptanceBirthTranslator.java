package uk.nhs.digital.nhsconnect.nhais.translator;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.service.EdifactToFhirService;

import java.util.List;

@Component
public class AcceptanceBirthTranslator implements FhirToEdifactTranslator {

    @Override
    public List<Segment> translate(Parameters parameters) throws FhirValidationException {
        return null;
    }
}
