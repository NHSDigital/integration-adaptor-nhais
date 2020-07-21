package uk.nhs.digital.nhsconnect.nhais.sequence;

import org.springframework.data.repository.CrudRepository;
import uk.nhs.digital.nhsconnect.nhais.sequence.OutboundSequenceId;

public interface SequenceDao extends CrudRepository<OutboundSequenceId, String> {
}