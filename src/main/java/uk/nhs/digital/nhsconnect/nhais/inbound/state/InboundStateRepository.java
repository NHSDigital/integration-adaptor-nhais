package uk.nhs.digital.nhsconnect.nhais.inbound.state;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;

import java.util.Optional;

@Repository
public interface InboundStateRepository extends CrudRepository<InboundState, String> {
    @Query("{ 'workflowId' : ?0, 'sender' : ?1, 'recipient' : ?2, 'interchangeSequence' : ?3, 'messageSequence' : ?4, 'transactionNumber' : ?5}")
    Optional<InboundState> findBy(WorkflowId workflowId, String sender, String recipient, Long interchangeSequence, Long messageSequence, Long transactionNumber);
}
