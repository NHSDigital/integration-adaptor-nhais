package uk.nhs.digital.nhsconnect.nhais.outbound.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.outbound.state.OutboundStateRepository;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@DirtiesContext
public class OutboundStateRepositoryTest {

    @Autowired
    OutboundStateRepository outboundStateRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    NhaisMongoClientConfiguration mongoConfig;

    @Test
    void whenDuplicateInterchangeOutboundStateInserted_thenThrowsException() {
        var outboundState = new OutboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L)
            .setMessageSequence(234L);
        var duplicateOutboundState = new OutboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L)
            .setMessageSequence(234L);

        assertInsert(outboundState, duplicateOutboundState);
    }

    @Test
    void whenDuplicateRecepOutboundStateInserted_thenThrowsException() {
        var outboundState = new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L);
        var duplicateOutboundState = new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(123L);

        assertInsert(outboundState, duplicateOutboundState);
    }

    @Test
    void when_ApplicationStarts_then_TtlIndexExistsWithValueFromConfiguration() {
        var indexOperations = mongoTemplate.indexOps(OutboundState.class);
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

    private void assertInsert(OutboundState first, OutboundState second) {
        outboundStateRepository.save(first);
        assertThat(outboundStateRepository.existsById(first.getId())).isTrue();

        assertThatThrownBy(() -> outboundStateRepository.save(second))
            .isInstanceOf(DuplicateKeyException.class);
    }
}
