package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The semi-structured previous address of the patient. Any non-blank address lines are translated as data elements.
 * Empty address lines are skipped and not represented with empty EDIFACT data elements (consecutive : characters)
 */
@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class PersonOldAddress extends Segment {
    private final static String KEY = "NAD";
    private final static String PAT_CODE = "PER";
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String addressLine5;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        String address = Stream.of(addressLine1, addressLine2, addressLine3, addressLine4, addressLine5)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(COLON_SEPARATOR));

        return PAT_CODE
            .concat(PLUS_SEPARATOR)
            .concat(PLUS_SEPARATOR)
            .concat(address);
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        Stream<String> addressLines = Stream.of(this.addressLine1, addressLine2, addressLine3, addressLine4, addressLine5);
        if (addressLines.allMatch(StringUtils::isBlank)) {
            throw new EdifactValidationException("At least 1 address line in patient's previous address should not be blank");
        }
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        // nothing
    }
}
