package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Example PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'
 */

@Getter
@Builder
public class PersonPreviousName extends Segment {

    public static final String KEY = "PNA";
    public static final String QUALIFIER = "PER";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;

    //all properties are optional
    private final String previousFamilyName;

    public static PersonPreviousName fromString(String edifactString) {
        if (!edifactString.startsWith(PersonPreviousName.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + PersonPreviousName.class.getSimpleName() + " from " + edifactString);
        }
        return PersonPreviousName.builder()
//            .previousFamilyName(extractNamePart("SU", edifactString))
            .build();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        List<String> values = new ArrayList<>();
        values.add(QUALIFIER);

        String namesDelimiter = containsName() ? "++" : "";

        Optional.ofNullable(this.previousFamilyName)
            .filter(StringUtils::isNotBlank)
            .map(value -> "SU:" + value)
            .ifPresent(values::add);

        return String.join(PLUS_SEPARATOR, values);
    }

    private boolean containsName() {
        return Stream.of(this.previousFamilyName)
            .anyMatch(Objects::nonNull);
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        //NOP
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }
}
