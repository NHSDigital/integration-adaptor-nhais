package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@DirtiesContext
public class OutboundStateRepositoryTest {

    @Autowired
    OutboundStateRepository outboundStateRepository;

    @Test
    void whenDuplicateOutboundStateInserted_thenThrowsException() {
        var outboundState = new OutboundState()
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setSendInterchangeSequence(123L)
            .setSendMessageSequence(234L);
        var duplicateOutboundState = new OutboundState()
            .setSender("some_sender")
            .setRecipient("some_recipient")
            .setSendInterchangeSequence(123L)
            .setSendMessageSequence(234L);

        outboundStateRepository.save(outboundState);
        assertThat(outboundStateRepository.existsById(outboundState.getId())).isTrue();

        assertThatThrownBy(() -> outboundStateRepository.save(duplicateOutboundState))
            .isInstanceOf(DuplicateKeyException.class);
    }
}
