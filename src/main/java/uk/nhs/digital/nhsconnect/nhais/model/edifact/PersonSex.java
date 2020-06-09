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
    private final static Map<Enumerations.AdministrativeGender, String> PATIENT_SEX_CODE = ImmutableMap.of(
            Enumerations.AdministrativeGender.UNKNOWN, "0",
            Enumerations.AdministrativeGender.MALE, "1",
            Enumerations.AdministrativeGender.FEMALE, "2",
            Enumerations.AdministrativeGender.OTHER, "9"
    );

    //PDI+1'
    private @NonNull String sexCode;

    public static String getGenderCode(Patient patient) {
        return Optional.ofNullable(PATIENT_SEX_CODE.get(patient.getGender()))
                .orElseThrow(() -> new NoSuchElementException("sex code not found: " + patient.getGender().getDisplay()));
    }

    @Override
    public String getKey() {
        return "PDI";
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
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }

        if (!PATIENT_SEX_CODE.containsValue(sexCode)) {
            throw new EdifactValidationException("Gender code not known: " + sexCode);
        }
    }
}
