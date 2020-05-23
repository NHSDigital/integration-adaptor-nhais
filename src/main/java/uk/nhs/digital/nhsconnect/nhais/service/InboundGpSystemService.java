package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import java.nio.charset.StandardCharsets;

@Component @Slf4j
public class InboundGpSystemService {

    @Autowired
    private FhirParser fhirParser;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${nhais.amqp.exchange}")
    private String exchange;

    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    private String gpSystemInboundQueueName;

    public void publishToSupplierQueue(Parameters parameters) {
        String jsonMessage = fhirParser.encodeToString(parameters);
        LOGGER.debug("Encoded FHIR to string: {}", jsonMessage);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setContentType("application/json");
        Message message = MessageBuilder
                .withBody(jsonMessage.getBytes(StandardCharsets.UTF_8))
                .andProperties(messageProperties).build();
        rabbitTemplate.send(exchange, gpSystemInboundQueueName, message);
        LOGGER.debug("Published message to inbound gp system queue");
    }

}
