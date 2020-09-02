package uk.nhs.digital.nhsconnect.nhais.pcrm;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@Slf4j
public class PcrmScenarios {

    private static final long WAIT_FOR_IN_SECONDS = 10 * 60; // 10 minutes * 60 seconds
    @Value("${nhais.amqp.gpSystemInboundQueueName}")
    protected String gpSystemInboundQueueName;
    @Autowired
    protected JmsTemplate jmsTemplate;
    @Value("${nhais.test.hostAndPort}")
    String hostAndPort;
    @Value("classpath:pcrm/acceptance-approval.json")
    Resource acceptanceApproval;
    @Value("classpath:pcrm/amendment.fhir.json")
    Resource amendment;

    @BeforeEach
    public void before() {
        clearInboundGpSystemQueue();
    }

    @PostConstruct
    private void postConstruct() {
        this.jmsTemplate.setReceiveTimeout(1000);
    }

    @Test
    void when_acceptance_then_approval() throws Exception {
        String requestBody = new String(Files.readAllBytes(acceptanceApproval.getFile().toPath()));
//        requestBody = replaceNhsNumber(requestBody);
        String operationId;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            var request = new HttpPost("http://" + hostAndPort + "/fhir/Patient/$nhais.acceptance");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(requestBody));
            try (CloseableHttpResponse response = client.execute(request)) {
                String content = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                LOGGER.debug(content);
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
                operationId = response.getFirstHeader("OperationId").getValue();
                LOGGER.info("OperationId: {}", operationId);
            }
        }
        var inboundQueueMessage = waitFor(this::getGpSystemInboundQueueMessage);
        assertThat(inboundQueueMessage.getStringProperty("OperationId")).isEqualTo(operationId);
        assertThat(inboundQueueMessage.getStringProperty("TransactionType")).isEqualTo("approval");
    }

    @Test
    void when_amendment_then_() throws Exception {
        String requestBody = new String(Files.readAllBytes(amendment.getFile().toPath()));
        String operationId;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            var request = new HttpPatch("http://" + hostAndPort + "/fhir/Patient/9692186105");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(requestBody));
            try (CloseableHttpResponse response = client.execute(request)) {
                String content = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                LOGGER.debug(content);
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
                operationId = response.getFirstHeader("OperationId").getValue();
                LOGGER.info("OperationId: {}", operationId);
            }
        }
//        var inboundQueueMessage = waitFor(this::getGpSystemInboundQueueMessage);
//        assertThat(inboundQueueMessage.getStringProperty("OperationId")).isEqualTo(operationId);
//        assertThat(inboundQueueMessage.getStringProperty("TransactionType")).isEqualTo("amendment");
    }

    private String replaceNhsNumber(String value) {
        return value.replace("%%NHS_NUMBER%%", NhsNumberGenerator.generateUniqueNhsNumber());
    }

    protected <T> T waitFor(Supplier<T> supplier) {
        var dataToReturn = new AtomicReference<T>();
        await()
            .atMost(WAIT_FOR_IN_SECONDS, SECONDS)
            .pollInterval(10, SECONDS)
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

    protected void clearInboundGpSystemQueue() {
        final Counter counter = new Counter();
        await()
            .atMost(10, SECONDS)
            .pollInterval(10, MILLISECONDS)
            .until(() -> {
                var result = getGpSystemInboundQueueMessage();
                if (result != null) {
                    counter.count++;
                    return false;
                } else {
                    return true;
                }
            });
        LOGGER.info("Cleared {} messages from inbound gp system queue", counter.count);
    }

    @SneakyThrows
    protected Message getGpSystemInboundQueueMessage() {
        Message message = jmsTemplate.receive(gpSystemInboundQueueName);
        if (message != null) {
            LOGGER.debug("Received message TransactionType: {} OperationId: {}",
                message.getStringProperty("TransactionType"),
                message.getStringProperty("OperationId"));
        } else {
            LOGGER.debug("JmsTemplate returned null message");
        }
        return message;
    }

    private static class Counter {
        public int count = 0;
    }

}
