package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

import static uk.nhs.digital.nhsconnect.nhais.utils.TimestampUtils.getCurrentDateTimeInISOFormat;

@Component
public class OutboundMeshService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${nhais.amqp.exchange}")
    private String exchange;

    @Value("${nhais.amqp.meshOutboundQueueName}")
    private String meshOutboundQueueName;

    public void send(MeshMessage message) {
        // use routingKey == queueName by convention
        message.setMessageSentTimestamp(getCurrentDateTimeInISOFormat());
        rabbitTemplate.convertAndSend(exchange, meshOutboundQueueName, message);
    }

}
