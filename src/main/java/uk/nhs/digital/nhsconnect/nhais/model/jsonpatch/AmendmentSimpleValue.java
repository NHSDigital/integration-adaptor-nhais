package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AmendmentSimpleValue implements AmendmentValue{

    private final String value;

    @Override
    public String get() {
        return value;
    }
}
