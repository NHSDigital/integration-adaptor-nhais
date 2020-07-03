package uk.nhs.digital.nhsconnect.nhais.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.awaitility.Durations;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.service.InboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.service.JmsReader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@ExtendWith({SpringExtension.class, SoftAssertionsExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
public abstract class MeshServiceBaseTest {

    public static final String DLQ_PREFIX = "DLQ.";
    protected static final int WAIT_FOR_IN_SECONDS = 5;
    private static final int RECEIVE_TIMEOUT = 5000;
    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);
    @Autowired
    protected JmsTemplate jmsTemplate;
    @Autowired
    protected InboundStateRepository inboundStateRepository;
    @Autowired
    protected OutboundStateRepository outboundStateRepository;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected MeshClient meshClient;
    @Autowired
    protected MeshConfig meshConfig;
    @Value("${nhais.amqp.meshInboundQueueName}")
    protected String meshInboundQueueName;
    @Value("${nhais.amqp.meshOutboundQueueName}")
    protected String meshOutboundQueueName;
    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    protected String gpSystemInboundQueueName;
    @Autowired
    private InboundQueueService inboundQueueService;
    private long originalReceiveTimeout;

    @PostConstruct
    private void postConstruct() {
        originalReceiveTimeout = this.jmsTemplate.getReceiveTimeout();
        this.jmsTemplate.setReceiveTimeout(RECEIVE_TIMEOUT);
    }

    @PreDestroy
    private void preDestroy() {
        this.jmsTemplate.setReceiveTimeout(originalReceiveTimeout);
    }

    protected void sendToMeshInboundQueue(MeshMessage meshMessage) {
        inboundQueueService.publish(meshMessage);
    }

    protected void sendToMeshInboundQueue(String data) {
        jmsTemplate.send(meshInboundQueueName, session -> session.createTextMessage(data));
    }

    @SneakyThrows
    protected Message getGpSystemInboundQueueMessage() {
        return jmsTemplate.receive(gpSystemInboundQueueName);
    }

    @SneakyThrows
    protected Message getDeadLetterInboundQueueMessage() {
        return jmsTemplate.receive(DLQ_PREFIX + meshInboundQueueName);
    }

    protected IBaseResource parseGpInboundQueueMessage(Message message) throws JMSException {
        var body = parseTextMessage(message);
        return new FhirParser().parse(body);
    }

    protected String parseTextMessage(Message message) throws JMSException {
        if (message == null) {
            return null;
        }
        return JmsReader.readMessage(message);
    }

    protected <T> T waitFor(Supplier<T> supplier) {
        var dataToReturn = new AtomicReference<T>();
        await()
            .atMost(WAIT_FOR_IN_SECONDS, SECONDS)
            .pollInterval(100, MILLISECONDS)
            .pollDelay(250, MILLISECONDS)
            .until(() -> {
                var data = supplier.get();
                if (data != null) {
                    dataToReturn.set(data);
                    return true;
                }
                return false;
            });

        return dataToReturn.get();
    }

    protected void clearMeshMailbox() {
        await().atMost(10, TimeUnit.SECONDS)
            .pollDelay(Durations.FIVE_SECONDS)
            .until(this::acknowledgeAllMeshMessages);
    }

    private Boolean acknowledgeAllMeshMessages() {
        // acknowledge message will remove it from MESH
        meshClient.getInboxMessageIds()
            .forEach(id -> meshClient.acknowledgeMessage(id));
        return meshClient.getInboxMessageIds().isEmpty();
    }
}
