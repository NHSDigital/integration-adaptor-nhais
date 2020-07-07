package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class AmendmentSimpleValue implements AmendmentValue{

    private final String value;

    @Override
    public String get() {
        return value;
    }
}
