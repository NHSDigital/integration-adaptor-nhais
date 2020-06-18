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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Example PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'
 */

@Getter
@Builder
public class PersonName extends Segment {

    public static final String KEY = "PNA";
    public static final String QUALIFIER = "PAT";
    public static final String KEY_QUALIFIER = KEY + PLUS_SEPARATOR + QUALIFIER;

    //all properties are optional
    private final String nhsNumber;
    private final PatientIdentificationType patientIdentificationType;
    private final String familyName;
    private final String forename;
    private final String title;
    private final String middleName;
    private final String thirdForename;

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
        String[] components = Split.byPlus(edifactString);
        if (components.length > 2 && StringUtils.isNotEmpty(components[2])) {
            return Split.byColon(components[2])[0];
        }
        return null;
    }

    private static PatientIdentificationType getPatientIdentificationType(String edifactString) {
        String[] components = Split.byPlus(edifactString);
        if (StringUtils.isNotEmpty(extractNhsNumber(edifactString)) && components.length > 1) {
            return PatientIdentificationType.valueOf(Split.byColon(components[2])[1]);
        }
        return null;
    }

    private static String extractNamePart(String qualifier, String text) {
        return Arrays.stream(Split.byPlus(text))
            .filter(value -> value.startsWith(qualifier))
            .map(value -> Split.byColon(value)[1])
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

    public Optional<NhsIdentifier> getNhsNumber() {
        return Optional.ofNullable(nhsNumber).map(NhsIdentifier::new);
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

    public enum PatientIdentificationType {
        OPI("Official Patient Identifier"),
        API("Amended Patient Identifier");

        private final String description;

        PatientIdentificationType(String description) {
            this.description = description;
        }
    }
}
