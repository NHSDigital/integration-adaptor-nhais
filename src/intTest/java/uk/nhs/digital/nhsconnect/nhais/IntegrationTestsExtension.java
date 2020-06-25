package uk.nhs.digital.nhsconnect.nhais;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.container.ActiveMqContainer;
import uk.nhs.digital.nhsconnect.nhais.container.FakeMeshContainer;
import uk.nhs.digital.nhsconnect.nhais.container.MongoDbContainer;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.SequenceDao;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.springframework.jms.support.destination.JmsDestinationAccessor.RECEIVE_TIMEOUT_NO_WAIT;
import static uk.nhs.digital.nhsconnect.nhais.jms.MeshServiceBaseTest.DLQ_PREFIX;

@Slf4j
public class IntegrationTestsExtension implements BeforeAllCallback, BeforeEachCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        ActiveMqContainer.getInstance().start();
        MongoDbContainer.getInstance().start();
        FakeMeshContainer.getInstance().start();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        var applicationContext = SpringExtension.getApplicationContext(context);

        var jmsTemplate = applicationContext.getBean(JmsTemplate.class);

        var meshInboundQueueName = Objects.requireNonNull(
            applicationContext.getEnvironment().getProperty("nhais.amqp.meshInboundQueueName"));
        var meshOutboundQueueName = Objects.requireNonNull(
            applicationContext.getEnvironment().getProperty("nhais.amqp.meshOutboundQueueName"));
        var gpSystemInboundQueueName = Objects.requireNonNull(
            applicationContext.getEnvironment().getProperty("nhais.amqp.meshInboundQueueName"));

        var receiveTimeout = jmsTemplate.getReceiveTimeout();
        jmsTemplate.setReceiveTimeout(RECEIVE_TIMEOUT_NO_WAIT);
        List.of(meshInboundQueueName, meshOutboundQueueName, gpSystemInboundQueueName)
            .stream()
            .map(queueName -> List.of(queueName, DLQ_PREFIX + queueName))
            .flatMap(Collection::stream)
            .forEach(queueName -> {
                while (jmsTemplate.receive(queueName) != null) {
                    LOGGER.info("Purged '" + queueName + "' message");
                }
            });
        jmsTemplate.setReceiveTimeout(receiveTimeout);

        var outboundStateRepository = applicationContext.getBean(OutboundStateRepository.class);
        var inboundStateRepository = applicationContext.getBean(InboundStateRepository.class);
        var sequenceRepository = applicationContext.getBean(SequenceDao.class);
        outboundStateRepository.deleteAll();
        inboundStateRepository.deleteAll();
        sequenceRepository.deleteAll();
    }
}
