package uk.nhs.digital.nhsconnect.nhais.outbound.state;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OutboundStateRepositoryExtensionsImpl implements OutboundStateRepositoryExtensions {

    private final MongoOperations mongoOperations;

    @Override
    public Optional<OutboundState> updateRecepDetails(UpdateRecepParams updateRecepParams) {

        var result = mongoOperations.findAndModify(
            updateRecepParams.buildQuery(),
            updateRecepParams.buildUpdate(),
            OutboundState.class);

        return Optional.ofNullable(result);
    }


}
