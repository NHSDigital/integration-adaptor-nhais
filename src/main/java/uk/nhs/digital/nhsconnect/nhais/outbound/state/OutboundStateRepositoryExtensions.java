package uk.nhs.digital.nhsconnect.nhais.outbound.state;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
        private final OutboundState.Recep recep;

        public Update buildUpdate() {
            return new Update().set("recep", recep);
        }

        public Query buildQuery() {
            return query(where("sndr").is(sender)
                .and("recip").is(recipient)
                .and("intSeq").is(interchangeSequence)
                .and("msgSeq").is(messageSequence)
                .and("recep").exists(false));
        }
    }


}
