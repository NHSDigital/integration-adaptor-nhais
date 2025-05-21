package uk.nhs.digital.nhsconnect.nhais.sequence;

import org.springframework.data.repository.CrudRepository;

public interface SequenceDao extends CrudRepository<OutboundSequenceId, String> {
}