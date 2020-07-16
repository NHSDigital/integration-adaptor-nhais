package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class PersonAddress extends Segment {
    // NAD+PAT++:::ORPINGTON:'
    private final static String KEY = "NAD";
    private final static String QUALIFIER = "PAT";
    public final static String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;
    private final static int POSTAL_CODE_OFFSET = 5;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String addressLine5;
    private String postalCode;

    public static PersonAddress fromString(String edifactString) {
        if (!edifactString.startsWith(PersonAddress.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + PersonAddress.class.getSimpleName() + " from " + edifactString);
        }
        String[] addressParts = Split.byPlus(edifactString);
        String[] addressLines = Split.byColon(addressParts[3]);

        var builder = PersonAddress.builder();
        if (addressLines.length > 0) {
            builder.addressLine1(addressLines[0]);
        }
        if (addressLines.length > 1) {
            builder.addressLine2(addressLines[1]);
        }
        if (addressLines.length > 2) {
            builder.addressLine3(addressLines[2]);
        }
        if (addressLines.length > 3) {
            builder.addressLine4(addressLines[3]);
        }
        if (addressLines.length > 4) {
            builder.addressLine5(addressLines[4]);
        }

        if (addressParts.length > 8) {
            builder.postalCode(addressParts[8]);
        }
        return builder.build();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        String address = Stream.of(addressLine1, addressLine2, addressLine3, addressLine4, addressLine5)
            .map(StringUtils::defaultString)
            .collect(Collectors.joining(COLON_SEPARATOR));

        String value = QUALIFIER
            .concat(PLUS_SEPARATOR)
            .concat(PLUS_SEPARATOR)
            .concat(address);

        if (StringUtils.isNotBlank(postalCode)) {
            value = value
                .concat(PLUS_SEPARATOR.repeat(POSTAL_CODE_OFFSET))
                .concat(postalCode);
        }

        return value;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (!isAddressLineValid(addressLine1) && !isAddressLineValid(addressLine2)) {
            throw new EdifactValidationException("Address line 1 or Address line 2 must be populated");
        }
    }

    private boolean isAddressLineValid(String addressLine) {
        return Objects.nonNull(addressLine) && !addressLine.isBlank();
    }
}
