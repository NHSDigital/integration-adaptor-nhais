package uk.nhs.digital.nhsconnect.nhais.outbound.state;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public interface OutboundStateRepositoryExtensions {
    Optional<OutboundState> updateRecepDetails(@NonNull UpdateRecepParams updateRecepParams);

    @RequiredArgsConstructor
    @Getter
    @ToString
    @NonNull
    class UpdateRecepParams {
        private final String sender;
        private final String recipient;
        private final Long interchangeSequence;
        private final Long messageSequence;
        private final ReferenceMessageRecep.RecepCode recepCode;
        private final Instant recepDateTime;

        public Update buildUpdate() {
            return new Update()
                .set("recepCode", recepCode)
                .set("recepDateTime", recepDateTime);
        }

        public Query buildQuery() {
            return query(where("sender").is(sender)
                .and("recipient").is(recipient)
                .and("interchangeSequence").is(interchangeSequence)
                .and("messageSequence").is(messageSequence)
                .and("recepCode").exists(false)
                .and("recepDateTime").exists(false));
        }
    }


}
