package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.configuration.NhaisMongoClientConfiguration;
import uk.nhs.digital.nhsconnect.nhais.configuration.ttl.TimeToLiveConfiguration;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.utils.TimestampService;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class InboundStateRepositoryTest {

    @Autowired
    InboundStateRepository inboundStateRepository;

    @Autowired
    TimestampService timestampService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    NhaisMongoClientConfiguration mongoConfig;

    @Test
    void whenDuplicateInterchangeInboundStateInserted_thenThrowsException() {
        var inboundState = new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L)
            .setMessageSequence(234L);
        var duplicateInboundState = new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L)
            .setMessageSequence(234L);

        assertInsert(inboundState, duplicateInboundState);
    }

    @Test
    void whenDuplicateRecepInboundStateInserted_thenThrowsException() {
        var inboundState = new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L);
        var duplicateInboundState = new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L);

        assertInsert(inboundState, duplicateInboundState);
    }

    @Test
    void when_ApplicationStarts_then_TtlIndexExistsWithValueFromConfiguration() {
        var indexOperations = mongoTemplate.indexOps(InboundState.class);
        assertThat(timeToLiveIndexExists(indexOperations)).isTrue();
    }

    private boolean timeToLiveIndexExists(IndexOperations indexOperations) {
        return indexOperations.getIndexInfo()
            .stream()
            .filter(index -> index.getName().equals(TimeToLiveConfiguration.TTL_INDEX_NAME))
            .map(IndexInfo::getExpireAfter)
            .flatMap(Optional::stream)
            .anyMatch(indexExpire -> indexExpire.compareTo(Duration.parse(mongoConfig.getTtl())) == 0);
    }

    private void assertInsert(InboundState first, InboundState second) {
        inboundStateRepository.save(first);

        assertThat(inboundStateRepository.existsById(first.getId())).isTrue();

        assertThatThrownBy(() -> inboundStateRepository.save(second))
            .isInstanceOf(DuplicateKeyException.class);
    }
}
