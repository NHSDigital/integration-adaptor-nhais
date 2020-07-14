package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum PatientIdentificationType {
    OFFICIAL_PATIENT_IDENTIFICATION("OPI"),
    AMENDED_PATIENT_IDENTIFICATION("API");

    @Getter
    private final String code;

    PatientIdentificationType(String code) {
        this.code = code;
    }

    public static PatientIdentificationType fromCode(String code) {
        return Arrays.stream(PatientIdentificationType.values())
            .filter(patientIdentificationType -> patientIdentificationType.getCode().equals(code))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(String.format("%s element not found", code)));
    }
}
