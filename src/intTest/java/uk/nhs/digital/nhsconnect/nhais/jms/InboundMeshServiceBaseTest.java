package uk.nhs.digital.nhsconnect.nhais.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.container.ActiveMqInitializer;
import uk.nhs.digital.nhsconnect.nhais.container.MongoDbInitializer;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.repository.DataType;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import javax.annotation.PostConstruct;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@ExtendWith({SpringExtension.class, SoftAssertionsExtension.class})
@ContextConfiguration(initializers = { ActiveMqInitializer.class, MongoDbInitializer.class })
@SpringBootTest
@Slf4j
@DirtiesContext
public abstract class InboundMeshServiceBaseTest {

    protected static final int WAIT_FOR_IN_SECONDS = 5;
    private static final int RECEIVE_TIMEOUT = 5000;

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
        this.jmsTemplate.setReceiveTimeout(RECEIVE_TIMEOUT);
    }

    protected void sendToMeshInboundQueue(MeshMessage meshMessage) {
        jmsTemplate.send(meshInboundQueueName, session -> session.createTextMessage(serializeMeshMessage(meshMessage)));
    }

    @SneakyThrows
    private String serializeMeshMessage(MeshMessage meshMessage) {
        return objectMapper.writeValueAsString(meshMessage);
    }

    protected InboundState waitForInboundState(
        DataType dataType, String sender, String recipient, long interchangeSequence, Long messageSequence) {

        Supplier<InboundState> getData = () -> inboundStateRepository.findBy(dataType, sender, recipient, interchangeSequence, messageSequence);

        await()
            .atMost(WAIT_FOR_IN_SECONDS, SECONDS)
            .pollInterval(50, MILLISECONDS)
            .until(() -> getData.get() != null);

        return getData.get();
    }
}
