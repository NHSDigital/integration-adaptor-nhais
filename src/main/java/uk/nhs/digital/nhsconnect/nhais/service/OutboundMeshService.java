package uk.nhs.digital.nhsconnect.nhais.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

@Component
public class OutboundMeshService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${nhais.amqp.exchange}")
    private String exchange;

    @Value("${nhais.amqp.routingkey}")
    private String routingkey;

    public void send(MeshMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingkey, message);
    }

}
