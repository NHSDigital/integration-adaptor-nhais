package uk.nhs.digital.nhsconnect.nhais.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;


@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentController {

    @PatchMapping(path= "/fhir/Patient/{nhsNumber}", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> amendment(@PathVariable(name= "nhsNumber") String nhsNumber, @RequestBody AmendmentBody amendmentBody) {
        LOGGER.info("Amendment request: {}", amendmentBody);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
