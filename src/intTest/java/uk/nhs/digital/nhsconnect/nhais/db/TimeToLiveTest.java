package uk.nhs.digital.nhsconnect.nhais.db;

import org.awaitility.Durations;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.configuration.NhaisMongoClientConfiguration;
import uk.nhs.digital.nhsconnect.nhais.configuration.ttl.MongoTtlCreator;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class TimeToLiveTest {

    @Autowired
    private InboundStateRepository inboundStateRepository;

    @Autowired
    private OutboundStateRepository outboundStateRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NhaisMongoClientConfiguration mongoConfig;

    @Test
    void when_ApplicationStarts_then_TtlIndexExistsForInboundStateWithValueFromConfiguration() {
        var indexOperations = mongoTemplate.indexOps(InboundState.class);
        assertThat(timeToLiveIndexExists(indexOperations)).isTrue();
    }

    @Test
    void when_ApplicationStarts_then_TtlIndexExistsForOutboundStateWithValueFromConfiguration() {
        var indexOperations = mongoTemplate.indexOps(OutboundState.class);
        assertThat(timeToLiveIndexExists(indexOperations)).isTrue();
    }

    private boolean timeToLiveIndexExists(IndexOperations indexOperations) {
        return indexOperations.getIndexInfo()
            .stream()
            .filter(index -> index.getName().equals(MongoTtlCreator.TTL_INDEX_NAME))
            .map(IndexInfo::getExpireAfter)
            .flatMap(Optional::stream)
            .anyMatch(indexExpire -> indexExpire.compareTo(Duration.parse(mongoConfig.getTtl())) == 0);
    }

    @Test
    @Disabled("Long running test that depends on external TTL config, enable when needed")
    void when_TimeToLiveHasPassedInInboundState_then_documentRemoved() {
        var inboundState = new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L)
            .setTranslationTimestamp(Instant.now().atZone(ZoneId.systemDefault()).toInstant());

        assertThat(inboundStateRepository.findAll()).isEmpty();
        inboundStateRepository.save(inboundState);
        assertThat(inboundStateRepository.findAll()).isNotEmpty();
        await()
            .atMost(90, TimeUnit.SECONDS)
            .pollInterval(Durations.ONE_SECOND)
            .untilAsserted(() -> assertThat(inboundStateRepository.findAll()).isEmpty());
    }

    @Test
    @Disabled("Long running test that depends on external TTL config, enable when needed")
    void when_TimeToLiveHasPassedInOutboundState_then_documentRemoved() {
        var inboundState = new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L)
            .setTranslationTimestamp(Instant.now().atZone(ZoneId.systemDefault()).toInstant());

        assertThat(outboundStateRepository.findAll()).isEmpty();
        outboundStateRepository.save(inboundState);
        assertThat(outboundStateRepository.findAll()).isNotEmpty();
        await()
            .atMost(90, TimeUnit.SECONDS)
            .pollInterval(Durations.ONE_SECOND)
            .untilAsserted(() -> assertThat(outboundStateRepository.findAll()).isEmpty());
    }
}
