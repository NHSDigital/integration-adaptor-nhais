package uk.nhs.digital.nhsconnect.nhais.repository;

import uk.nhs.digital.nhsconnect.nhais.model.mesh.WorkflowId;

import java.util.Optional;

public interface InboundStateRepositoryExtensions {
    Optional<InboundState> findBy(WorkflowId workflowId, String sender, String recipient, Long interchangeSequence, Long messageSequence, Long transactionNumber);
}
