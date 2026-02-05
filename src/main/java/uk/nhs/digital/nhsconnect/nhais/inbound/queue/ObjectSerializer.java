package uk.nhs.digital.nhsconnect.nhais.inbound.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ObjectSerializer {
    private final FhirParser fhirParser;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String serialize(Object object) {
        if (object instanceof IBaseResource iBaseResource) {
            return fhirParser.encodeToString(iBaseResource);
        }
        if (object instanceof AmendmentBody amendmentBody) {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(amendmentBody);
        }
        throw new UnsupportedOperationException("Data type " + object.getClass().getSimpleName() + " is not supported");
    }
}
