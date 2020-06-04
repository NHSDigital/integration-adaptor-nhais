package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import uk.nhs.digital.nhsconnect.nhais.model.sequence.OutboundSequenceId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SequenceRepositoryTest {
    private final static String MAX_KEY = "max-key";
    private final static String NEW_KEY = "new-key";
    private final static OutboundSequenceId SEQUENCE_ID = new OutboundSequenceId(NEW_KEY, 1L);

    @InjectMocks
    private SequenceRepository sequenceRepository;

    @Mock
    private MongoOperations mongoOperations;

    @Test
    public void When_GetNextKey_Expect_CorrectValue() {
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
                eq(OutboundSequenceId.class))).thenReturn(SEQUENCE_ID);

        assertThat(sequenceRepository.getNext(NEW_KEY)).isEqualTo(1L);
    }

    @Test
    public void When_GetMaxNextKey_Expect_ValueReset() {
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
            eq(OutboundSequenceId.class)))
            .thenReturn(new OutboundSequenceId(MAX_KEY, 100000000L))
            .thenReturn(new OutboundSequenceId(MAX_KEY, 100000001L));

        assertThat(sequenceRepository.getNext(MAX_KEY)).isEqualTo(1L);
    }
}