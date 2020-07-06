package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.Amendment;

@RestController
@Slf4j
public class AmendmentController {

    @PatchMapping(path="/fhir/Patient/{id}", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OperationOutcome amendment(@PathVariable(name="id") String id, @RequestBody Amendment amendment) {
        LOGGER.info("Amendment request: {}", amendment);
        return null;
    }

}
