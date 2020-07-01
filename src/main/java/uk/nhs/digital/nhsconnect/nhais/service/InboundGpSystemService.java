package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
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

    public void publishToSupplierQueue(DataToSend dataToSend) {

        String jsonMessage = fhirParser.encodeToString(dataToSend.getParameters());
        LOGGER.debug("Encoded FHIR to string: {}", jsonMessage);
        jmsTemplate.send(gpSystemInboundQueueName, session -> {
            var message = session.createTextMessage(jsonMessage);
            message.setStringProperty(JmsHeaders.OPERATION_ID, dataToSend.getOperationId());
            message.setStringProperty(JmsHeaders.TRANSACTION_TYPE, dataToSend.getTransactionType().name().toLowerCase());
            return message;
        });
        LOGGER.debug("Published message to inbound gp system queue");
    }

    @Builder
    @Getter
    public static class DataToSend {
        private final Parameters parameters;
        private final String operationId;
        private final ReferenceTransactionType.TransactionType transactionType;
    }
}
