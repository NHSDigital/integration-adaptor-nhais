package uk.nhs.digital.nhsconnect.nhais.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.exception.UnknownWorkflowException;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import java.io.IOException;

@Component @Slf4j
public class InboundMeshService {

    @Autowired
    private RegistrationConsumerService registrationConsumerService;

    @Autowired
    private RecepConsumerService recepConsumerService;

    @RabbitListener(queues = "#{'${nhais.amqp.meshInboundQueueName}'.split(',')}", ackMode = "MANUAL")
    public void handleInboundMessage(Message<MeshMessage> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        MeshMessage meshMessage = message.getPayload();
        // TODO: get the correlation id and attach to logger?
        if(WorkflowId.REGISTRATION.equals(meshMessage.getWorkflowId())) {
            registrationConsumerService.handleRegistration(meshMessage);
        } else if(WorkflowId.RECEP.equals(meshMessage.getWorkflowId())) {
            recepConsumerService.handleRecep(meshMessage);
        } else {
            throw new UnknownWorkflowException(meshMessage.getWorkflowId());
        }
        LOGGER.info(message.toString());
        LOGGER.info(message.getPayload().toString());
        // do stuff that works
        // TODO: deadletter if it doesn't work?
        channel.basicAck(tag, false);
    }

}
