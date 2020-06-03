package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.nhsconnect.nhais.container.MongoDbInitializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(initializers = MongoDbInitializer.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class InboundStateRepositoryTest {

    @Autowired
    InboundStateRepository inboundStateRepository;

    @Test
    void whenDuplicateInterchangeInboundStateInserted_thenThrowsException() {
        var inboundState = new InboundState()
            .setDataType(DataType.INTERCHANGE)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setReceiveInterchangeSequence(123L)
            .setReceiveMessageSequence(234L);
        var duplicateInboundState = new InboundState()
            .setDataType(DataType.INTERCHANGE)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setReceiveInterchangeSequence(123L)
            .setReceiveMessageSequence(234L);

        assertInsert(inboundState, duplicateInboundState);
    }

    @Test
    void whenDuplicateRecepInboundStateInserted_thenThrowsException() {
        var inboundState = new InboundState()
            .setDataType(DataType.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setReceiveInterchangeSequence(123L);
        var duplicateInboundState = new InboundState()
            .setDataType(DataType.RECEP)
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setReceiveInterchangeSequence(123L);

        assertInsert(inboundState, duplicateInboundState);
    }

    private void assertInsert(InboundState first, InboundState second) {
        inboundStateRepository.save(first);

        assertThat(inboundStateRepository.existsById(first.getId())).isTrue();

        assertThatThrownBy(() -> inboundStateRepository.save(second))
            .isInstanceOf(DuplicateKeyException.class);
    }
}
