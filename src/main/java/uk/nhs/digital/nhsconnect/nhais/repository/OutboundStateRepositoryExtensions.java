package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;

import java.time.Instant;
import java.util.Optional;

public interface OutboundStateRepositoryExtensions {
    Optional<OutboundState> updateRecepDetails(@NonNull UpdateRecepParams updateRecepParams);

    @RequiredArgsConstructor
    @Getter
    @ToString
    @NonNull
    class UpdateRecepDetailsQueryParams {
        private final String sender;
        private final String recipient;
        private final Long interchangeSequence;
        private final Long messageSequence;
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    @NonNull
    class UpdateRecepDetails {
        private final ReferenceMessageRecep.RecepCode recepCode;
        private final Instant recepDateTime;
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    @NonNull
    class UpdateRecepParams {
        private final UpdateRecepDetailsQueryParams updateRecepDetailsQueryParams;
        private final UpdateRecepDetails updateRecepDetails;
    }
}
