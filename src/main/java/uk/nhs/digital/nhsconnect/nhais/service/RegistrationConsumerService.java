package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;

@Component @Slf4j
public class RegistrationConsumerService {

    @Autowired
    private EdifactParser edifactParser;

    @Autowired
    private EdifactToFhirService edifactToFhirService;

    @Autowired
    private InboundGpSystemService inboundGpSystemService;

    public void handleRegistration(MeshMessage meshMessage) {
        LOGGER.debug("Received Registration message: {}", meshMessage);
        Interchange interchange = edifactParser.parse(meshMessage.getContent());
        LOGGER.debug("Parsed registration message into interchange: {}", interchange);
        // recep producer service
        // inbound state management service
        Parameters outputParameters = edifactToFhirService.convertToFhir(interchange);
        LOGGER.debug("Converted registration message into FHIR: {}", outputParameters);
        inboundGpSystemService.publishToSupplierQueue(outputParameters);
        LOGGER.debug("Published inbound registration message to gp supplier queue");

    }

}
