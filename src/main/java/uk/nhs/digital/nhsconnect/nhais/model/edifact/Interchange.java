package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class Interchange {

    // TODO: stub for the internal representation of an edifact interchange
    @Singular
    private List<Segment> segments;

}
