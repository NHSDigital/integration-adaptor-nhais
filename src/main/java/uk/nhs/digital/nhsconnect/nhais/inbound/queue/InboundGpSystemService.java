package uk.nhs.digital.nhsconnect.nhais.inbound.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsHeaders;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InboundGpSystemService {

    private final ObjectSerializer serializer;
    private final JmsTemplate jmsTemplate;

    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    private String gpSystemInboundQueueName;

    public void publishToSupplierQueue(DataToSend dataToSend) {

        String jsonMessage = serializer.serialize(dataToSend.getContent());
        LOGGER.debug("Encoded FHIR to string: {}", jsonMessage);
        jmsTemplate.send(gpSystemInboundQueueName, session -> {
            var message = session.createTextMessage(jsonMessage);
            message.setStringProperty(JmsHeaders.OPERATION_ID, dataToSend.getOperationId());
            message.setStringProperty(JmsHeaders.TRANSACTION_TYPE, dataToSend.getTransactionType().name().toLowerCase());
            return message;
        });
        LOGGER.debug("Published message to inbound gp system queue");
    }

    public static class DataToSend {
        @Getter
        private Object content;
        @Getter
        @Setter
        private String operationId;
        @Getter
        @Setter
        private ReferenceTransactionType.TransactionType transactionType;

        public DataToSend setContent(Parameters parameters) {
            this.content = parameters;
            return this;
        }

        public DataToSend setContent(AmendmentBody amendmentBody) {
            this.content = amendmentBody;
            return this;
        }
    }
}
