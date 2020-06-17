package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Example PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'
 */
@Setter
@Getter
@Builder
public class PersonName extends Segment {

    public static final String KEY = "PNA";
    public static final String QUALIFIER = "PAT";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;

    //all properties are optional
    private String nhsNumber;
    private String patientIdentificationType;
    private String familyName;
    private String forename;
    private String title;
    private String middleName;
    private String thirdForename;

    public static PersonName fromString(String edifactString) {
        if (!edifactString.startsWith(PersonName.KEY_QUALIFIER)) {
            throw new IllegalArgumentException("Can't create " + PersonName.class.getSimpleName() + " from " + edifactString);
        }
        return PersonName.builder()
            .nhsNumber(extractNhsNumber(edifactString))
            .patientIdentificationType(getPatientIdentificationType(edifactString))
            .familyName(extractNamePart("SU", edifactString))
            .forename(extractNamePart("FO", edifactString))
            .title(extractNamePart("TI", edifactString))
            .middleName(extractNamePart("MI", edifactString))
            .thirdForename(extractNamePart("FS", edifactString))
            .build();
    }

    private static String extractNhsNumber(String edifactString) {
        String[] components = edifactString.split("\\+");
        if (components.length > 2 && StringUtils.isNotEmpty(components[2])) {
            return components[2].split(":")[0];
        }
        return null;
    }

    private static String getPatientIdentificationType(String edifactString) {
        String[] components = edifactString.split("\\+");
        if (StringUtils.isNotEmpty(extractNhsNumber(edifactString)) && components.length > 1) {
            return components[2].split(":")[1];
        }
        return null;
    }

    private static String extractNamePart(String qualifier, String text) {
        return Arrays.stream(text.split("\\+"))
            .filter(value -> value.startsWith(qualifier))
            .map(value -> value.split(":")[1])
            .findFirst()
            .orElse(null);
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
        Optional.ofNullable(this.nhsNumber)
            .map(value -> value + ":" + this.patientIdentificationType + namesDelimiter)
            .ifPresentOrElse(values::add, () -> values.add(namesDelimiter));

        Optional.ofNullable(this.familyName)
            .map(value -> "SU:" + value)
            .ifPresent(values::add);
        Optional.ofNullable(this.forename)
            .map(value -> "FO:" + value)
            .ifPresent(values::add);
        Optional.ofNullable(this.title)
            .map(value -> "TI:" + value)
            .ifPresent(values::add);
        Optional.ofNullable(this.middleName)
            .map(value -> "MI:" + value)
            .ifPresent(values::add);
        Optional.ofNullable(this.thirdForename)
            .map(value -> "FS:" + value)
            .ifPresent(values::add);

        return String.join(PLUS_SEPARATOR, values);
    }

    private boolean containsName() {
        return Stream.of(this.familyName, this.forename, this.title, this.middleName, this.thirdForename)
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
