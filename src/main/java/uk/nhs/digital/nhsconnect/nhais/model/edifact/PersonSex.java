package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Builder
@Data
public class PersonSex extends Segment {
    private final static String KEY = "PDI";
    private final static Map<Enumerations.AdministrativeGender, String> PATIENT_SEX_CODE = ImmutableMap.of(
        Enumerations.AdministrativeGender.UNKNOWN, Gender.UNKNOWN.toString(),
        Enumerations.AdministrativeGender.MALE, Gender.MALE.toString(),
        Enumerations.AdministrativeGender.FEMALE, Gender.FEMALE.toString(),
        Enumerations.AdministrativeGender.OTHER, Gender.OTHER.toString()
    );

    //PDI+1'
    private @NonNull String sexCode;

    public static String getGenderCode(Patient patient) {
        return Optional.ofNullable(PATIENT_SEX_CODE.get(patient.getGender()))
            .orElseThrow(() -> new NoSuchElementException("sex code not found: " + patient.getGender().getDisplay()));
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return sexCode;
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(sexCode) || sexCode.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Gender code is required");
        }

        if (!PATIENT_SEX_CODE.containsValue(sexCode)) {
            throw new EdifactValidationException(getKey() + "Gender code not known: " + sexCode);
        }
    }

    private enum Gender {
        UNKNOWN("0"),
        MALE("1"),
        FEMALE("2"),
        OTHER("9");

        String code;

        Gender(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }
}
