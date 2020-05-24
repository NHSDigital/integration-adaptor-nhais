package uk.nhs.digital.nhsconnect.nhais.service;

import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateDAO;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FhirToEdifactService {

    public interface Segment { String toEdifact(); } // TODO: remove and replace with Segment abstract class when merged

    public enum TransactionType {} // TODO: remove and replace with edifact model when implemented

    @Autowired
    private OutboundStateRepository outboundStateRepository;

    @Autowired
    private SequenceService sequenceService;

    public TranslatedInterchange convertToEdifact(Patient patient, String operationId, TransactionType transactionType) {
        TranslationItems translationItems = new TranslationItems();
        translationItems.patient = patient;
        translationItems.operationId = operationId;
        translationItems.transactionType = transactionType;
        extractDetailsFromPatient(translationItems);
        createSegments(translationItems);
        prevalidateSegments(translationItems);
        generateSequenceNumbers(translationItems);
        recordOutboundState(translationItems);
        addSequenceNumbersToSegments(translationItems);
        return translateInterchange(translationItems);
    }

    private void extractDetailsFromPatient(TranslationItems translationItems) {
        // set sender and recipient
    }

    private void createSegments(TranslationItems translationItems) {
        // TODO: create each segment needed for EDIFACT state MVC (those created by Python app)
        translationItems.segments = Collections.emptyList();
    }

    private void prevalidateSegments(TranslationItems translationItems) {

    }

    private void generateSequenceNumbers(TranslationItems translationItems) {

    }

    private void recordOutboundState(TranslationItems translationItems) {
        OutboundStateDAO outboundStateDAO = new OutboundStateDAO();
        // TODO: set other DAO fields
        outboundStateDAO.setOperationId(translationItems.operationId);
        outboundStateRepository.save(outboundStateDAO);
    }

    private void addSequenceNumbersToSegments(TranslationItems translationItems) {

    }

    private TranslatedInterchange translateInterchange(TranslationItems translationItems) {
        // toEdifact() also calls validate()
        String edifact = translationItems.segments.stream().map(Segment::toEdifact).collect(Collectors.joining("\n"));
        TranslatedInterchange interchange = new TranslatedInterchange();
        interchange.setEdifact(edifact);
        return interchange;
    }

    private static class TranslationItems {
        private Patient patient;
        private TransactionType transactionType;
        private List<Segment> segments;
        private String sender;
        private String recipient;
        private String operationId;
        private Long sendMessageSequence;
        private Long sendInterchangeSequence;
        private Long transactionNumber;

    }

}
