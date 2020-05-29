package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EntityNotFoundException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;

import java.time.Instant;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OutboundStateRepositoryExtensionsImpl implements OutboundStateRepositoryExtensions {

    private final MongoOperations mongoOperations;

    @Override
    @NonNull
    public void updateRecep(
        String sender,
        String recipient,
        Long interchangeSequence,
        Long messageSequence,
        ReferenceMessageRecep.RecepCode recepCode,
        Instant recepDateTime) {

        var query = query(where("sender").is(sender)
            .and("recipient").is(recipient)
            .and("sendInterchangeSequence").is(interchangeSequence)
            .and("sendMessageSequence").is(messageSequence));

        var result = mongoOperations.findAndModify(
            query,
            new Update()
                .set("recepCode", recepCode)
                .set("recepDateTime", recepDateTime),
            OutboundState.class);

        if (result == null) {
            throw new EntityNotFoundException(query.toString());
        }
    }
}
