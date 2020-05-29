package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.utils.Headers;

@Component @Slf4j
public class InboundGpSystemService {

    @Autowired
    private FhirParser fhirParser;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    private String gpSystemInboundQueueName;

    public void publishToSupplierQueue(Parameters parameters, String operationId) {
        String jsonMessage = fhirParser.encodeToString(parameters);
        LOGGER.debug("Encoded FHIR to string: {}", jsonMessage);
        jmsTemplate.convertAndSend(gpSystemInboundQueueName, jsonMessage, message -> {
            message.setStringProperty(Headers.OPERATION_ID, operationId);
            return message;
        });
        LOGGER.debug("Published message to inbound gp system queue");
    }
}
