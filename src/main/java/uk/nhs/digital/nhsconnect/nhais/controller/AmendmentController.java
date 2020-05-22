package uk.nhs.digital.nhsconnect.nhais.controller;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AmendmentController {

    @PatchMapping(path="/fhir/Patient/{id}", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OperationOutcome amendment(@PathVariable(name="id") String id, @RequestBody String jsonPatch) {
        return null;
    }

}
