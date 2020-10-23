package uk.nhs.digital.nhsconnect.nhais;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.queue.InboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.mesh.RecipientMailboxIdMappings;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshClient;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshConfig;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshHeaders;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshHttpClientBuilder;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshRequests;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.utils.JmsReader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, SoftAssertionsExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@Slf4j
public abstract class IntegrationBaseTest {

    public static final String DLQ_PREFIX = "DLQ.";
    protected static final int WAIT_FOR_IN_SECONDS = 10;
    protected static final int POLL_INTERVAL_MS = 100;
    protected static final int POLL_DELAY_MS = 10;
    private static final int JMS_RECEIVE_TIMEOUT = 500;
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
    @Autowired
    private RecipientMailboxIdMappings recipientMailboxIdMappings;
    @Autowired
    private MeshHeaders meshHeaders;
    @Autowired
    private MeshHttpClientBuilder meshHttpClientBuilder;
    @Value("${nhais.amqp.meshInboundQueueName}")
    protected String meshInboundQueueName;
    @Value("${nhais.amqp.meshOutboundQueueName}")
    protected String meshOutboundQueueName;
    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    protected String gpSystemInboundQueueName;
    @Autowired
    private InboundQueueService inboundQueueService;
    private long originalReceiveTimeout;
    protected MeshClient nhaisMeshClient;

    @PostConstruct
    private void postConstruct() {
        originalReceiveTimeout = this.jmsTemplate.getReceiveTimeout();
        this.jmsTemplate.setReceiveTimeout(JMS_RECEIVE_TIMEOUT);
        nhaisMeshClient = buildMeshClientForNhaisMailbox();
    }

    @PreDestroy
    private void preDestroy() {
        this.jmsTemplate.setReceiveTimeout(originalReceiveTimeout);
    }

    protected void sendToMeshInboundQueue(InboundMeshMessage meshMessage) {
        inboundQueueService.publish(meshMessage);
    }

    protected void sendToMeshInboundQueue(String data) {
        jmsTemplate.send(meshInboundQueueName, session -> session.createTextMessage(data));
    }

    @SneakyThrows
    protected Message getGpSystemInboundQueueMessage() {
        return waitFor(() -> jmsTemplate.receive(gpSystemInboundQueueName));
    }

    @SneakyThrows
    protected Message getDeadLetterMeshInboundQueueMessage(String queueName) {
        return waitFor(() -> jmsTemplate.receive(DLQ_PREFIX + queueName));
    }

    @SneakyThrows
    protected void clearDeadLetterQueue(String queueName) {
        waitForCondition(() -> jmsTemplate.receive(DLQ_PREFIX + queueName) == null);
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
            .pollInterval(POLL_INTERVAL_MS, MILLISECONDS)
            .pollDelay(POLL_DELAY_MS, MILLISECONDS)
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

    protected void waitForCondition(Supplier<Boolean> supplier) {
        await()
            .atMost(WAIT_FOR_IN_SECONDS, SECONDS)
            .pollInterval(POLL_INTERVAL_MS, MILLISECONDS)
            .pollDelay(POLL_DELAY_MS, MILLISECONDS)
            .until(supplier::get);
    }

    protected void clearGpSystemInboundQueue() {
        waitForCondition(() -> jmsTemplate.receive(gpSystemInboundQueueName) == null);

    }

    protected void clearMeshMailboxes() {
        waitForCondition(() -> acknowledgeAllMeshMessages(meshClient));
        waitForCondition(() -> acknowledgeAllMeshMessages(nhaisMeshClient));
    }

    protected InboundMeshMessage waitForMeshMessage(MeshClient meshClient) {
        List<String> messageIds = waitFor(() -> {
            List<String> inboxMessageIds = meshClient.getInboxMessageIds();
            return inboxMessageIds.isEmpty() ? null : inboxMessageIds;
        });
        return meshClient.getEdifactMessage(messageIds.get(0));
    }

    private Boolean acknowledgeAllMeshMessages(MeshClient meshClient) {
        // acknowledge message will remove it from MESH
        meshClient.getInboxMessageIds().forEach(meshClient::acknowledgeMessage);
        return meshClient.getInboxMessageIds().isEmpty();
    }

    /**
     * This MeshClient is "inverted" so that is can act as an NHAIS system. It receives messages on the nhais mailbox
     * and sends them to the gp mailbox;
     */
    @SneakyThrows
    private MeshClient buildMeshClientForNhaisMailbox() {
        // getting this from config is
        String nhaisMailboxId = recipientMailboxIdMappings.getRecipientMailboxId(new MeshMessage().setHaTradingPartnerCode("XX11"));
        String gpMailboxId = meshConfig.getMailboxId();
        RecipientMailboxIdMappings mockRecipientMailboxIdMappings = mock(RecipientMailboxIdMappings.class);
        when(mockRecipientMailboxIdMappings.getRecipientMailboxId(any(OutboundMeshMessage.class))).thenReturn(gpMailboxId);
        // getters perform a transformation
        String endpointCert = (String) FieldUtils.readField(meshConfig, "endpointCert", true);
        String endpointPrivateKey = (String) FieldUtils.readField(meshConfig, "endpointPrivateKey", true);
        String subCaCert = (String) FieldUtils.readField(meshConfig, "subCAcert", true);
        MeshConfig nhaisMailboxConfig = new MeshConfig(nhaisMailboxId, meshConfig.getMailboxPassword(),
            meshConfig.getSharedKey(), meshConfig.getHost(), meshConfig.getCertValidation(), endpointCert,
            endpointPrivateKey, subCaCert);
        MeshHeaders meshHeaders = new MeshHeaders(nhaisMailboxConfig);
        MeshRequests meshRequests = new MeshRequests(nhaisMailboxConfig, meshHeaders);
        return new MeshClient(meshRequests, mockRecipientMailboxIdMappings, meshHttpClientBuilder);
    }
}
