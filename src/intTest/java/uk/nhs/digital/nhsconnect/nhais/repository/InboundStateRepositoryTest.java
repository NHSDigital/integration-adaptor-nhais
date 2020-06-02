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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(initializers = MongoDbInitializer.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class InboundStateRepositoryTest {

    @Autowired
    InboundStateRepository inboundStateRepository;

    @Test
    void whenDuplicateInboundStateInserted_thenThrowsException() {
        var inboundState = new InboundState()
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setReceiveInterchangeSequence(123L)
            .setReceiveMessageSequence(234L);
        var duplicateInboundState = new InboundState()
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setReceiveInterchangeSequence(123L)
            .setReceiveMessageSequence(234L);

        inboundStateRepository.save(inboundState);
        assertTrue(inboundStateRepository.existsById(inboundState.getId()));

        assertThrows(DuplicateKeyException.class, () -> inboundStateRepository.save(duplicateInboundState));
    }
}
