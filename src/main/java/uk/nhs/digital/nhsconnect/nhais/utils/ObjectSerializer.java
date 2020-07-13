package uk.nhs.digital.nhsconnect.nhais.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ObjectSerializer {
    private final FhirParser fhirParser;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String serialize(Object object) {
        if (object instanceof IBaseResource) {
            return fhirParser.encodeToString((IBaseResource) object);
        }
        if (object instanceof AmendmentBody) {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }
        throw new UnsupportedOperationException("Data type " + object.getClass().getSimpleName() + " is not supported");
    }
}
