package uk.nhs.digital.nhsconnect.nhais.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;

import java.time.Instant;

public interface OutboundStateRepositoryExtensions {
    void updateRecepDetails(
        UpdateRecepDetailsQueryParams updateRecepDetailsQueryParams,
        UpdateRecepDetails updateRecepDetails);

    @RequiredArgsConstructor
    @Getter
    class UpdateRecepDetailsQueryParams {
        private final String sender;
        private final String recipient;
        private final Long interchangeSequence;
        private final Long messageSequence;
    }

    @RequiredArgsConstructor
    @Getter
    class UpdateRecepDetails {
        private final ReferenceMessageRecep.RecepCode recepCode;
        private final Instant recepDateTime;
    }
}
