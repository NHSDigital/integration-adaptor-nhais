package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EntityNotFoundException;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OutboundStateRepositoryExtensionsImpl implements OutboundStateRepositoryExtensions {

    private final MongoOperations mongoOperations;

    @Override
    public OutboundState updateRecepDetails(
        UpdateRecepDetailsQueryParams updateRecepDetailsQueryParams,
        UpdateRecepDetails updateRecepDetails) {

        var query = query(where("sender").is(updateRecepDetailsQueryParams.getSender())
            .and("recipient").is(updateRecepDetailsQueryParams.getRecipient())
            .and("sendInterchangeSequence").is(updateRecepDetailsQueryParams.getInterchangeSequence())
            .and("sendMessageSequence").is(updateRecepDetailsQueryParams.getMessageSequence()));

        var result = mongoOperations.findAndModify(
            query,
            new Update()
                .set("recepCode", updateRecepDetails.getRecepCode())
                .set("recepDateTime", updateRecepDetails.getRecepDateTime()),
            OutboundState.class);

        if (result == null) {
            throw new EntityNotFoundException(query.toString());
        }

        return result;
    }
}
