package uk.nhs.digital.nhsconnect.nhais.controller;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.nhs.digital.nhsconnect.nhais.fhir.FhirParser;

@RestController
public class AcceptanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptanceController.class);
    private final FhirParser fhirParser;

    @Autowired
    public AcceptanceController(FhirParser fhirParser) {
        this.fhirParser = fhirParser;
    }

    @PostMapping(path = "/fhir/Patient/{id}", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OperationOutcome acceptance(@PathVariable(name = "id") String id, @RequestBody String body) {
        Patient patient = fhirParser.parse(body);
        LOGGER.info("FhirParser example: " + fhirParser.encodeToString(patient));
        return null;
    }
}
