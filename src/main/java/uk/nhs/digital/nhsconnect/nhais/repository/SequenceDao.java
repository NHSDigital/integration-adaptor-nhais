package uk.nhs.digital.nhsconnect.nhais.repository;

import org.springframework.data.repository.CrudRepository;
import uk.nhs.digital.nhsconnect.nhais.model.sequence.SequenceId;

public interface SequenceDao extends CrudRepository<SequenceId, String> {
}
