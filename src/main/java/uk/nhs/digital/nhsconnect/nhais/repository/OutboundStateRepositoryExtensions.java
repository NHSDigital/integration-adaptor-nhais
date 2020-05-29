package uk.nhs.digital.nhsconnect.nhais.repository;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceMessageRecep;

import java.time.ZonedDateTime;

public interface OutboundStateRepositoryExtensions {
    void updateRecep(
        String sender,
        String recipient,
        Long interchangeSequence,
        Long messageSequence,
        ZonedDateTime dateTime,
        ReferenceMessageRecep.RecepCode recepCode);
}
