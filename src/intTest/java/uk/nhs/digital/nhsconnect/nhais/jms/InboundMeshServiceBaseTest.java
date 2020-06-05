package uk.nhs.digital.nhsconnect.nhais.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@ExtendWith({SpringExtension.class, SoftAssertionsExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j

public abstract class InboundMeshServiceBaseTest {

    protected static final int WAIT_FOR_IN_SECONDS = 5;
    private long originalReceiveTimeout;
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
    private ObjectMapper objectMapper;

    @Value("${nhais.amqp.meshInboundQueueName}")
    protected String meshInboundQueueName;

    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    protected String gpSystemInboundQueueName;

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
        jmsTemplate.send(meshInboundQueueName, session -> session.createTextMessage(serializeMeshMessage(meshMessage)));
    }

    @SneakyThrows
    private String serializeMeshMessage(MeshMessage meshMessage) {
        return objectMapper.writeValueAsString(meshMessage);
    }

    protected <T> T waitFor(Supplier<T> supplier) {
        var dataToReturn = new AtomicReference<T>();
        await()
            .atMost(WAIT_FOR_IN_SECONDS, SECONDS)
            .pollInterval(50, MILLISECONDS)
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
}
