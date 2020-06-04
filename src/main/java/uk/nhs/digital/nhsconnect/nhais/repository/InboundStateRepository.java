package uk.nhs.digital.nhsconnect.nhais.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundStateRepository extends CrudRepository<InboundState, String> {
    InboundState findBy(
        DataType dataType, String sender, String recipient, long receiveInterchangeSequence, Long receiveMessageSequence);
}
