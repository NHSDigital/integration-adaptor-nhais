package uk.nhs.digital.nhsconnect.nhais.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

import static uk.nhs.digital.nhsconnect.nhais.utils.TimestampUtils.getCurrentDateTimeInISOFormat;

@Component
public class OutboundMeshService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${nhais.amqp.meshOutboundQueueName}")
    private String meshOutboundQueueName;

    @SneakyThrows
    public void send(MeshMessage message) {
        message.setMessageSentTimestamp(getCurrentDateTimeInISOFormat());
        jmsTemplate.convertAndSend(meshOutboundQueueName, message);
    }

}
