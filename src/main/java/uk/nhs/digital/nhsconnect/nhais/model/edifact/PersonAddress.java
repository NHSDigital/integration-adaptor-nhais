package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Data
public class PersonAddress extends Segment {
    private final static String NAME_AND_ADDRESS = "NAD";
    private final static String PAT_CODE = "PAT";
    private final static String COLON_SEPARATOR = ":";
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String addressLine5;

    @Override
    public String getKey() {
        return NAME_AND_ADDRESS;
    }

    @Override
    public String getValue() {
        String address = Stream.of(addressLine1, addressLine2, addressLine3, addressLine4, addressLine5)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(COLON_SEPARATOR));

        return PAT_CODE
            .concat(PLUS_SEPARATOR)
            .concat(PLUS_SEPARATOR)
            .concat(address);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(addressLine1) && Objects.isNull(addressLine2)) {
            throw new EdifactValidationException("Address line 1 or Address line 2 must be populated");
        }

        if (addressLine1.isBlank() && addressLine2.isBlank()) {
            throw new EdifactValidationException("Address line 1 or Address line 2 must be populated");
        }
    }
}
