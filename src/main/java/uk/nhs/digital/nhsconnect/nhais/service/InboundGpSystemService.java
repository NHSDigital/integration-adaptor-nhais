package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InboundGpSystemService {

    private final FhirParser fhirParser;
    private final JmsTemplate jmsTemplate;

    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    private String gpSystemInboundQueueName;

    public void publishToSupplierQueue(Parameters parameters, String operationId) {
        String jsonMessage = fhirParser.encodeToString(parameters);
        LOGGER.debug("Encoded FHIR to string: {}", jsonMessage);
        jmsTemplate.send(gpSystemInboundQueueName, session -> {
            var message = session.createTextMessage(jsonMessage);
            message.setStringProperty(JmsHeaders.OPERATION_ID, operationId);
            return message;
        });
        LOGGER.debug("Published message to inbound gp system queue");
    }
}
