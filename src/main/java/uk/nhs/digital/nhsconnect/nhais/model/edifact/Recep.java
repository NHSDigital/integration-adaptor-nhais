package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Recep {

    private final RecepMessage recepMessage;

    @Getter(lazy = true)
    private final InterchangeHeader interchangeHeader = recepMessage.getInterchangeHeader();
    @Getter(lazy = true)
    private final List<ReferenceMessageRecep> referenceMessageReceps = recepMessage.getReferenceMessageReceps();
    @Getter(lazy = true)
    private final ReferenceInterchangeRecep referenceInterchangeRecep = recepMessage.getReferenceInterchangeRecep();
    @Getter(lazy = true)
    private final DateTimePeriod dateTimePeriod = recepMessage.getDateTimePeriod();
}
