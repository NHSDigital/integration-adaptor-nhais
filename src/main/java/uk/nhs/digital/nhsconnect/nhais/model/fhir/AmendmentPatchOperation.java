package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public enum AmendmentPatchOperation {
    ADD("add"),
    REPLACE("replace"),
    REMOVE("remove");

    @JsonValue
    private final String operationName;
}
