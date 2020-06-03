package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.nhsconnect.nhais.model.sequence.OutboundSequenceId;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Repository
public class SequenceRepository {
    private final static String KEY = "key";
    private final static String SEQUENCE_NUMBER = "sequenceNumber";
    private final static long MAX_SEQUENCE_NUMBER = 100_000_000L;

    @Autowired
    private MongoOperations mongoOperations;

    public Long getNext(String key) {
        Long seqNumber = increment(key);
        if (seqNumber == 0) {
            seqNumber = increment(key);
        }
        return seqNumber;
    }

    private Long increment(String key) {
        return Objects.requireNonNull(mongoOperations.findAndModify(
                query(where(KEY).is(key)),
                new Update().inc(SEQUENCE_NUMBER, 1),
                options().returnNew(true).upsert(true),
                OutboundSequenceId.class))
                .getSequenceNumber() % MAX_SEQUENCE_NUMBER;
    }
}
