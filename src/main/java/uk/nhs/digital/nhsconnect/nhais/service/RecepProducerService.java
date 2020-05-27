package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;

import java.util.List;

@Component
public class RecepProducerService {
    // TODO: extend from an abstraction of FhirToEdifactService

    public void produceRecep(Interchange receivedInterchangeFromHa) {
        for(Segment segment : receivedInterchangeFromHa.getSegments()) {
            // grab interchange and message identifiers
        }

        List<Segment> recepMessageSegment;
        // add segments to produce recep message

    }

}
