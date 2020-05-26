package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateDAO;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RecepProducerService {
    // TODO: extend from an abstraction of FhirToEdifactService

    @Autowired
    private OutboundStateRepository outboundStateRepository;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private TimestampService timestampService;

    public void produceRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        // TODO convert
        mapEdifactToRecep(receivedInterchangeFromHa);

        //TODO


        //TODO update state
        recordOutboundState();

    }

    public List<Segment> mapEdifactToRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        for (Segment segment : receivedInterchangeFromHa.getSegments()) {
            LOGGER.info("segment to edifact: " + segment.toEdifact());
            // grab interchange and message identifiers
        }

        //map to recep:
        List<Segment> recepMessageSegments = new ArrayList<>();
        // add segments to produce recep message

        return recepMessageSegments;
    }

    private void recordOutboundState() {
        OutboundStateDAO outboundStateDAO = new OutboundStateDAO();
        outboundStateDAO.setRecipient("put recipient here");
        outboundStateDAO.setSender("put sender here");

        outboundStateDAO.setSendInterchangeSequence(123L); //put seq here
        outboundStateDAO.setSendMessageSequence(321L); //put seq here
        outboundStateDAO.setTransactionId(999L); //put seq here

        outboundStateDAO.setTransactionType("is it needed ? ");
        outboundStateDAO.setTransactionTimestamp(null); //put date here
        outboundStateDAO.setOperationId("put operation id here");
        outboundStateRepository.save(outboundStateDAO);
    }

}
