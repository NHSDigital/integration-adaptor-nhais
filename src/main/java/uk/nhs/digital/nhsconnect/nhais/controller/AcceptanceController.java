package uk.nhs.digital.nhsconnect.nhais.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.EdifactToMeshMessageService;
import uk.nhs.digital.nhsconnect.nhais.service.FhirToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.service.OutboundMeshService;

@RestController
public class AcceptanceController {

    @Autowired
    private OutboundMeshService outboundMeshService;

    @Autowired
    private FhirToEdifactService fhirToEdifactService;

    @Autowired
    private EdifactToMeshMessageService edifactToMeshMessageService;


    @PostMapping(path="/fhir/Patient/{id}", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OperationOutcome acceptance(@PathVariable(name="id") String id, @RequestBody String body) {
        FhirContext ctx = FhirContext.forR4(); // TODO: inject as singleton this is slow?
        IParser parser = ctx.newJsonParser();
        Patient parsed = parser.parseResource(Patient.class, body);
        TranslatedInterchange translatedInterchange = fhirToEdifactService.convertToEdifact(parsed);
        MeshMessage meshMessage = edifactToMeshMessageService.toMeshMessage(translatedInterchange);
        outboundMeshService.send(meshMessage);
        return null;
    }

}
