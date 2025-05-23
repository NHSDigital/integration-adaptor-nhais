package uk.nhs.digital.nhsconnect.nhais.inbound;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundState;
import uk.nhs.digital.nhsconnect.nhais.inbound.state.InboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class InboundStateRepositoryTest {
    private static final long INTERCHANGE_SEQUENCE = 123L;
    private static final long MESSAGE_SEQUENCE = 234L;

    @Autowired
    private InboundStateRepository inboundStateRepository;

    @Test
    void When_DuplicateInterchangeInboundStateInserted_Expect_ThrowsException() {
        var inboundState = new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setMessageSequence(MESSAGE_SEQUENCE);
        var duplicateInboundState = new InboundState()
            .setWorkflowId(WorkflowId.REGISTRATION)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(INTERCHANGE_SEQUENCE)
            .setMessageSequence(MESSAGE_SEQUENCE);

        assertInsert(inboundState, duplicateInboundState);
    }

    @Test
    void When_DuplicateRecepInboundStateInserted_Expect_ThrowsException() {
        var inboundState = new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(INTERCHANGE_SEQUENCE);
        var duplicateInboundState = new InboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setInterchangeSequence(INTERCHANGE_SEQUENCE);

        assertInsert(inboundState, duplicateInboundState);
    }

    private void assertInsert(InboundState first, InboundState second) {
        inboundStateRepository.save(first);

        assertThat(inboundStateRepository.existsById(first.getId())).isTrue();

        assertThatThrownBy(() -> inboundStateRepository.save(second))
            .isInstanceOf(DuplicateKeyException.class);
    }
}
