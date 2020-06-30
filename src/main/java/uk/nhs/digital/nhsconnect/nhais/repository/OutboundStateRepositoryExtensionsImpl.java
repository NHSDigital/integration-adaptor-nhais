package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OutboundStateRepositoryExtensionsImpl implements OutboundStateRepositoryExtensions {

    private final MongoOperations mongoOperations;

    @Override
    public Optional<OutboundState> updateRecepDetails(UpdateRecepParams updateRecepParams) {
        var result = mongoOperations.findAndModify(
            buildQuery(updateRecepParams.getUpdateRecepDetailsQueryParams()),
            buildUpdate(updateRecepParams.getUpdateRecepDetails()),
            OutboundState.class);

        return Optional.ofNullable(result);
    }

    private Update buildUpdate(UpdateRecepDetails updateRecepDetails) {
        return new Update()
            .set("recepCode", updateRecepDetails.getRecepCode())
            .set("recepDateTime", updateRecepDetails.getRecepDateTime());
    }

    private Query buildQuery(UpdateRecepDetailsQueryParams updateRecepDetailsQueryParams) {
        return query(where("sender").is(updateRecepDetailsQueryParams.getSender())
            .and("recipient").is(updateRecepDetailsQueryParams.getRecipient())
            .and("interchangeSequence").is(updateRecepDetailsQueryParams.getInterchangeSequence())
            .and("messageSequence").is(updateRecepDetailsQueryParams.getMessageSequence())
            .and("recepCode").exists(false)
            .and("recepDateTime").exists(false));
    }
}
