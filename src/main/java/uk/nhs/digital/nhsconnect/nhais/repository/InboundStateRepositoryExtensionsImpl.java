package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InboundStateRepositoryExtensionsImpl implements InboundStateRepositoryExtensions {

    private final MongoOperations mongoOperations;

    @Override
    public Optional<InboundState> findBy(
        WorkflowId workflowId, String sender, String recipient, Long interchangeSequence, Long messageSequence, Long transactionNumber) {

        var query = query(
            where("workflowId").is(workflowId)
                .and("sender").is(sender)
                .and("recipient").is(recipient)
                .and("interchangeSequence").is(interchangeSequence)
                .and("messageSequence").is(messageSequence)
                .and("transactionNumber").is(transactionNumber));

        var result = mongoOperations.find(query, InboundState.class);

        return result.stream()
            .findFirst();
    }
}
