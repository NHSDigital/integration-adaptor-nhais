package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.Getter;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;

import org.apache.commons.lang3.StringUtils;

/**
 * Example PAT+RAT56:OBI+++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'
 */
@Builder
public class PatientIdentifier extends Segment {

    public static final String KEY = "PNA";
    public static final String QUALIFIER = "PAT";
    public static final String KEY_QUALIFIER = KEY + "+" + QUALIFIER;

    //all properties are optional
    private final String nhsNumber;
    @Getter private final String patientIdentificationType;
    @Getter private final String familyName;
    @Getter private final String forename;
    @Getter private final String title;
    @Getter private final String middleName;
    @Getter private final String thirdForename;


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

    private boolean containsName(){
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

    public Optional<NhsIdentifier> getNhsNumber() {
        return Optional.ofNullable(nhsNumber).map(NhsIdentifier::new);
    }

    public static PatientIdentifier fromString(String edifactString) {
        if(!edifactString.startsWith(PatientIdentifier.KEY_QUALIFIER)){
            throw new IllegalArgumentException("Can't create " + PatientIdentifier.class.getSimpleName() + " from " + edifactString);
        }
        return PatientIdentifier.builder()
            .nhsNumber(getNhsNumber(edifactString))
            .patientIdentificationType(getPatientIdentificationType(edifactString))
            .familyName(extractNamePart("SU", edifactString))
            .forename(extractNamePart("FO", edifactString))
            .title(extractNamePart("TI", edifactString))
            .middleName(extractNamePart("MI", edifactString))
            .thirdForename(extractNamePart("FS", edifactString))
            .build();
    }

    private static String getNhsNumber(String edifactString) {
        String[] components = Split.byPlus(edifactString);
        if(components.length > 2 && StringUtils.isNotEmpty(components[2])) {
            return Split.byColon(components[2])[0];
        }
        return null;
    }

    private static String getPatientIdentificationType(String edifactString) {
        String[] components = Split.byPlus(edifactString);
        if(StringUtils.isNotEmpty(getNhsNumber(edifactString)) && components.length > 1) {
            return Split.byColon(components[2])[1];
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
}
