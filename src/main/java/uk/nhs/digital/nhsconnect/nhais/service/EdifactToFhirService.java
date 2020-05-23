package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;

@Component
public class EdifactToFhirService {

    Parameters convertToFhir(Interchange interchange) {
        return new Parameters();
    }

}
