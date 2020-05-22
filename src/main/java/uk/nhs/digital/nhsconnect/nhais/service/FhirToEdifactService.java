package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;

@Component
public class FhirToEdifactService {

    public TranslatedInterchange convertToEdifact(Patient patient) {
        TranslatedInterchange translatedInterchange = new TranslatedInterchange();
        translatedInterchange.setEdifact("EDIFACT");
        return translatedInterchange;
    }

}
