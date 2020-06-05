package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
public class OutboundStateRepositoryTest {

    @Autowired
    OutboundStateRepository outboundStateRepository;

    @Test
    void whenDuplicateInterchangeOutboundStateInserted_thenThrowsException() {
        var outboundState = new OutboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setSendInterchangeSequence(123L)
            .setSendMessageSequence(234L);
        var duplicateOutboundState = new OutboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setSendInterchangeSequence(123L)
            .setSendMessageSequence(234L);

        assertInsert(outboundState, duplicateOutboundState);
    }

    @Test
    void whenDuplicateRecepOutboundStateInserted_thenThrowsException() {
        var outboundState = new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setSendInterchangeSequence(123L);
        var duplicateOutboundState = new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setSendInterchangeSequence(123L);

        assertInsert(outboundState, duplicateOutboundState);
    }

    private void assertInsert(OutboundState first, OutboundState second) {
        outboundStateRepository.save(first);
        assertThat(outboundStateRepository.existsById(first.getId())).isTrue();

        assertThatThrownBy(() -> outboundStateRepository.save(second))
            .isInstanceOf(DuplicateKeyException.class);
    }
}
