package uk.nhs.digital.nhsconnect.nhais.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundStateRepository extends CrudRepository<OutboundState, String>, OutboundStateRepositoryExtensions {
}
