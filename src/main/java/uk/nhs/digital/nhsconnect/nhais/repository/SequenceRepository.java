package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.nhsconnect.nhais.exceptions.SequenceException;
import uk.nhs.digital.nhsconnect.nhais.model.sequence.SequenceId;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@AllArgsConstructor
@Repository
public class SequenceRepository {
    private final static String KEY = "key";
    private final static String SEQUENCE_NUMBER = "sequenceNumber";
    private final static Long MAX_SEQUENCE_NUMBER = 10000000L;

    private MongoOperations mongoOperations;
    private SequenceDao sequenceDao;

    public boolean existsByKey(String key) {
        return sequenceDao.existsById(key);
    }

    public Long addSequenceKey(String key) {
        sequenceDao.save(new SequenceId(key, 1L));
        return sequenceDao.findById(key)
                .orElseThrow(() -> new SequenceException("Exception when adding sequence key"))
                .getSequenceNumber();

    }

    public Long getNext(String key) {
        Long seqNumber = increment(key);
        if (seqNumber >= MAX_SEQUENCE_NUMBER) {
            LOGGER.info("Sequence number reset for key: {}", key);
            seqNumber = addSequenceKey(key);
        }

        return seqNumber;
    }

    private Long increment(String key) {
        return Objects.requireNonNull(mongoOperations.findAndModify(
                query(where(KEY).is(key)),
                new Update().inc(SEQUENCE_NUMBER, 1),
                options().returnNew(true).upsert(true),
                SequenceId.class))
                .getSequenceNumber();
    }
}
