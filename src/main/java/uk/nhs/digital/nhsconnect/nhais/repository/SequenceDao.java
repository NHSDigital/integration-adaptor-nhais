package uk.nhs.digital.nhsconnect.nhais.repository;

import org.springframework.data.repository.CrudRepository;
import uk.nhs.digital.nhsconnect.nhais.model.sequence.OutboundSequenceId;

public interface SequenceDao extends CrudRepository<OutboundSequenceId, String> {
}