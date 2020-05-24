package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateDAO;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

@Component
public class FhirToEdifactService {

    @Autowired
    private OutboundStateRepository outboundStateRepository;

    @Autowired
    private SequenceService sequenceService;

    public TranslatedInterchange convertToEdifact(Patient patient, String operationId) {
        OutboundStateDAO outboundStateDAO = new OutboundStateDAO();
        outboundStateDAO.setOperationId(operationId);
        outboundStateRepository.save(outboundStateDAO);
        TranslatedInterchange translatedInterchange = new TranslatedInterchange();
        translatedInterchange.setEdifact("EDIFACT");
        return translatedInterchange;
    }

}
