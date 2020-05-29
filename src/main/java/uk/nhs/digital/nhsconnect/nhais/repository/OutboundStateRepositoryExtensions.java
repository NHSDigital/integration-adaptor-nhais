package uk.nhs.digital.nhsconnect.nhais.repository;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;

import java.time.Instant;

public interface OutboundStateRepositoryExtensions {
    void updateRecep(
        String sender,
        String recipient,
        Long interchangeSequence,
        Long messageSequence,
        ReferenceMessageRecep.RecepCode recepCode,
        Instant recepDateTime);
}
