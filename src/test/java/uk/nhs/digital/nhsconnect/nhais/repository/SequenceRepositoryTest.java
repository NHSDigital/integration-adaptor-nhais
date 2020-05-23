package uk.nhs.digital.nhsconnect.nhais.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import uk.nhs.digital.nhsconnect.nhais.model.sequence.SequenceId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SequenceRepositoryTest {
    private final static String MAX_KEY = "max-key";
    private final static String NEW_KEY = "new-key";
    private final static SequenceId SEQUENCE_ID = new SequenceId(NEW_KEY, 1L);
    private SequenceRepository sequenceRepository;

    @Mock
    private MongoOperations mongoOperations;

    @Before
    public void setUp() {
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
                eq(SequenceId.class))).thenReturn(SEQUENCE_ID);

        sequenceRepository = new SequenceRepository(mongoOperations);
    }

    @Test
    public void When_GetNextKey_Expect_CorrectValue() {
        assertThat(sequenceRepository.getNext(NEW_KEY)).isEqualTo(1L);
    }

    @Test
    public void When_GetMaxNextKey_Expect_ValueReset() {
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
                eq(SequenceId.class)))
                .thenReturn(new SequenceId(MAX_KEY, 10000000L))
                .thenReturn(new SequenceId(MAX_KEY, 1L));

        assertThat(sequenceRepository.getNext(MAX_KEY)).isEqualTo(1L);
    }
}