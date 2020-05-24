package uk.nhs.digital.nhsconnect.nhais.parse;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FhirParserTest {

    private FhirParser fhirParser = new FhirParser();

    @Test
    public void when_validPatient_parsedSuccessfully() throws Exception {
        try(InputStream is = this.getClass().getResourceAsStream("/patient/patient.json")) {
            String payload = IOUtils.toString(is, StandardCharsets.UTF_8);
            Patient patient = fhirParser.parsePatient(payload);
            // TODO: validate patient? or trust that HAPI works...
        }
    }

    @Test
    public void when_emptyPatientPayload_throwsFhirValidationException() throws Exception {
        String payload = "{}";
        FhirValidationException ex = assertThrows(FhirValidationException.class, () -> fhirParser.parsePatient(payload));
        assertTrue(ex.getMessage().contains("missing required element: 'resourceType'"));
    }

    @Test @Disabled
    public void when_invalidId_throwsExceptionWithOperationOutcome() throws Exception {
        try(InputStream is = this.getClass().getResourceAsStream("/patient/patient_invalid_id.json")) {
            String payload = IOUtils.toString(is, StandardCharsets.UTF_8);
            Patient patient = fhirParser.parsePatient(payload);
            FhirValidationException ex = assertThrows(FhirValidationException.class, () -> fhirParser.parsePatient(payload));
            IBaseOperationOutcome operationOutcome = ex.getOperationOutcome();
        }
    }

}
