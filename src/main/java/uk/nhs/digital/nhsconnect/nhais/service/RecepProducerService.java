package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.InterchangeHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RecepProducerService {
    private static final String UNB_TRANSFER_SUFFIX = "+RECEP+++EDIFACT TRANSFER";

    // TODO: extend from an abstraction of FhirToEdifactService

//    @Autowired
//    private OutboundStateRepository outboundStateRepository;
//
//    @Autowired
//    private SequenceService sequenceService;

    public void produceRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        // TODO convert
        mapEdifactToRecep(receivedInterchangeFromHa);

        //TODO


        //TODO update state
        recordOutboundState();

    }

    public List<Segment> mapEdifactToRecep(Interchange receivedInterchangeFromHa) throws EdifactValidationException {
        List<Segment> recepMessageSegments = new ArrayList<>();
        recepMessageSegments.add(mapToRecipInterchangeHeader(receivedInterchangeFromHa));

        return recepMessageSegments;
    }

    private InterchangeHeader mapToRecipInterchangeHeader(Interchange interchange) {
        InterchangeHeader interchangeHeader = interchange.getInterchangeHeader();
        interchangeHeader.setTransferString(UNB_TRANSFER_SUFFIX);

        return interchangeHeader;
    }

    private void recordOutboundState() {
        //TODO
    }
}