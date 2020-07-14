package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.hl7.fhir.r4.model.Enumerations;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;

import java.util.Arrays;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
@Builder
@Data
public class PersonSex extends Segment {
    private final static String KEY = "PDI";

    //PDI+1'
    private @NonNull Gender gender;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return gender.getCode();
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (gender == null) {
            throw new EdifactValidationException(getKey() + ": Gender code is required");
        }
    }

    public enum Gender {
        UNKNOWN("0"),
        MALE("1"),
        FEMALE("2"),
        OTHER("9");

        private final static Map<Enumerations.AdministrativeGender, Gender> FROM_FHIR_MAP = ImmutableMap.of(
            Enumerations.AdministrativeGender.UNKNOWN, Gender.UNKNOWN,
            Enumerations.AdministrativeGender.MALE, Gender.MALE,
            Enumerations.AdministrativeGender.FEMALE, Gender.FEMALE,
            Enumerations.AdministrativeGender.OTHER, Gender.OTHER
        );

        @Getter
        private final String code;

        Gender(String code) {
            this.code = code;
        }

        public static Gender fromName(@NonNull String name) {
            return Arrays.stream(Gender.values())
                .filter(gender -> gender.name().toLowerCase().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No gender value for '" + name + "'"));
        }

        public static Gender fromFhir(Enumerations.AdministrativeGender fhirGender) {
            return FROM_FHIR_MAP.get(fhirGender);
        }

        @Override
        public String toString() {
            return code;
        }
    }
}
