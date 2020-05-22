package uk.nhs.digital.nhsconnect.nhais.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AcceptanceController {

    @PostMapping(path="/fhir/Patient/{id}", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OperationOutcome acceptance(@PathVariable(name="id") String id, @RequestBody String body) {
        FhirContext ctx = FhirContext.forR4(); // TODO: inject as singleton this is slow?
        IParser parser = ctx.newJsonParser();
        Patient parsed = parser.parseResource(Patient.class, body);
        return null;
    }

}
