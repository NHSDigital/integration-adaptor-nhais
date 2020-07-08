package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Example PNA+PER++++SU:PATTERSON'
 */

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@ToString
@Slf4j
public class PersonPreviousName extends Segment {

    public static final String KEY = "PNA";
    public static final String QUALIFIER = "PER";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;

    private final String previousFamilyName;

    public static PersonPreviousName fromString(String edifactString) {
        if (!edifactString.startsWith(PersonPreviousName.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + PersonPreviousName.class.getSimpleName() + " from " + edifactString);
        }

        var fields = Split.byPlus(edifactString);
        var builder = PersonPreviousName.builder();
        if (fields.length >= 6) {
            var fieldParts = Split.byColon(fields[5]);
            if (fieldParts.length == 2 && fieldParts[0].equals("SU")) {
                builder.previousFamilyName(fieldParts[1]);
            } else {
                throw new IllegalArgumentException("Illegal surname value");
            }
        }
        return builder.build();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        List<String> values = new ArrayList<>();
        values.add(QUALIFIER);
        values.addAll(IntStream.range(0, 3)
            .mapToObj(x -> StringUtils.EMPTY)
            .collect(Collectors.toList()));

        Optional.ofNullable(this.previousFamilyName)
            .filter(StringUtils::isNotBlank)
            .map(value -> "SU:" + value)
            .ifPresent(values::add);

        values = removeEmptyLeadingFields(values, StringUtils::isNotBlank);

        return String.join(PLUS_SEPARATOR, values);
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        //NOP
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
        //NOP
    }
}
