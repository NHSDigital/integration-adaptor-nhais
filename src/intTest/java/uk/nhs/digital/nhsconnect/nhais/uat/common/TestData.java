package uk.nhs.digital.nhsconnect.nhais.uat.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TestData {
    private final String edifact;
    private final String recep;
    private final String json;
}
