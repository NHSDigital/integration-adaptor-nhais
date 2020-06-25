package uk.nhs.digital.nhsconnect.nhais.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

@Repository
public interface InboundStateRepository extends CrudRepository<InboundState, String> {
    InboundState findBy(
        WorkflowId workflowId, String sender, String recipient, long receiveInterchangeSequence, Long receiveMessageSequence);

    InboundState findBy(
        WorkflowId workflowId, String sender, String recipient, long receiveInterchangeSequence, Long receiveMessageSequence, Long transactionNumber);
}
