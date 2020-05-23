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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SequenceRepositoryTest {
    private final static String TEST_KEY = "test-key";
    private final static String MAX_KEY = "max-key";
    private final static String NEW_KEY = "new-key";
    private final static SequenceId SEQUENCE_ID = new SequenceId(TEST_KEY, 1L);
    private SequenceRepository sequenceRepository;
   
    @Mock
    private MongoOperations mongoOperations;
    @Mock
    private SequenceDao sequenceDao;

    @Before
    public void setUp() {
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
                eq(SequenceId.class))).thenReturn(SEQUENCE_ID);

        when(sequenceDao.existsById(TEST_KEY)).thenReturn(true);
        when(sequenceDao.findById(TEST_KEY)).thenReturn(Optional.of(SEQUENCE_ID));
        when(sequenceDao.findById(NEW_KEY)).thenReturn(Optional.of(new SequenceId(NEW_KEY, 1L)));
        when(sequenceDao.findById(MAX_KEY)).thenReturn(Optional.of(new SequenceId(MAX_KEY, 1L)));

        sequenceRepository = new SequenceRepository(mongoOperations, sequenceDao);
    }

    @Test
    public void When_GetExistByKey_Expect_CorrectValue() {
        assertThat(sequenceRepository.existsByKey(TEST_KEY)).isTrue();
    }


    @Test
    public void When_GetNextKey_Expect_CorrectValue() {
        assertThat(sequenceRepository.getNext(TEST_KEY)).isEqualTo(1L);
    }

    @Test
    public void When_AddingNewSequenceKey_Expect_CorrectValue() {

        assertThat(sequenceRepository.addSequenceKey(NEW_KEY)).isEqualTo(1L);
    }

    @Test
    public void When_GetMaxNextKey_Expect_ValueReset() {
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class),
                eq(SequenceId.class))).thenReturn(new SequenceId(MAX_KEY, 10000000L));

        assertThat(sequenceRepository.getNext(MAX_KEY)).isEqualTo(1L);
    }
}