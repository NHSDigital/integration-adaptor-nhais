package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.utils.EdifactAddressPart;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The fully-structured present physical address of the patient
 * example: NAD+PAT++HOLLY COTTAGE:12 LONG LANE:LITTLE HAMLET:BROMLEY:KENT+++++BR5  4ER'
 */
@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class PersonAddress extends Segment {
    private static final String KEY = "NAD";
    private static final String QUALIFIER = "PAT";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;
    private static final int POSTAL_CODE_OFFSET = 5;
    private static final String EMPTY_FIRST_ADDRESS_LINE_PLACEHOLDER = "??";
    private static final int ADDRESS_LINE_INDEX = 3;

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
        String[] addressComponents = Split.byPlus(edifactString);
        String[] addressLinesParts =
            Split.byColon(addressComponents[ADDRESS_LINE_INDEX]);

        var builder = PersonAddress.builder();
        if (addressLinesParts.length > EdifactAddressPart.ADDRESS_LINE_1_INDEX) {
            String addressLine1 = replacePlaceholderWithEmpty(addressLinesParts[EdifactAddressPart.ADDRESS_LINE_1_INDEX]);
            builder.addressLine1(addressLine1);
        }
        if (addressLinesParts.length > EdifactAddressPart.ADDRESS_LINE_2_INDEX) {
            builder.addressLine2(addressLinesParts[EdifactAddressPart.ADDRESS_LINE_2_INDEX]);
        }
        if (addressLinesParts.length > EdifactAddressPart.ADDRESS_LINE_3_INDEX) {
            builder.addressLine3(addressLinesParts[EdifactAddressPart.ADDRESS_LINE_3_INDEX]);
        }
        if (addressLinesParts.length > EdifactAddressPart.ADDRESS_LINE_4_INDEX) {
            builder.addressLine4(addressLinesParts[EdifactAddressPart.ADDRESS_LINE_4_INDEX]);
        }
        if (addressLinesParts.length > EdifactAddressPart.ADDRESS_LINE_5_INDEX) {
            builder.addressLine5(addressLinesParts[EdifactAddressPart.ADDRESS_LINE_5_INDEX]);
        }

        if (addressComponents.length > EdifactAddressPart.POSTAL_CODE_INDEX) {
            builder.postalCode(addressComponents[EdifactAddressPart.POSTAL_CODE_INDEX]);
        }

        return builder.build();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        String addressLine1WithPossiblePlaceholder = replaceEmptyWithPlaceholder(addressLine1);
        String address = Stream.of(addressLine1WithPossiblePlaceholder, addressLine2, addressLine3, addressLine4, addressLine5)
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

    private static String replaceEmptyWithPlaceholder(String addressLine1) {
        if (StringUtils.isBlank(addressLine1)) {
            return EMPTY_FIRST_ADDRESS_LINE_PLACEHOLDER;
        }
        return addressLine1;
    }

    private static String replacePlaceholderWithEmpty(String addressLine1) {
        if (addressLine1.equals(EMPTY_FIRST_ADDRESS_LINE_PLACEHOLDER)) {
            return StringUtils.EMPTY;
        }
        return addressLine1;
    }
}
