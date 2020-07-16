package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AmendmentExtension implements AmendmentValue {

    @JsonProperty(value = "url")
    private final String url;

}
