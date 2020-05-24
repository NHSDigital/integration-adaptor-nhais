package uk.nhs.digital.nhsconnect.nhais.controller;

import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.TranslatedInterchange;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.service.EdifactToMeshMessageService;
import uk.nhs.digital.nhsconnect.nhais.service.FhirToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.service.OutboundMeshService;

import java.util.Collections;
import java.util.UUID;

@RestController
public class AcceptanceController {
    @Autowired
    private OutboundMeshService outboundMeshService;

    @Autowired
    private FhirToEdifactService fhirToEdifactService;

    @Autowired
    private EdifactToMeshMessageService edifactToMeshMessageService;

    @Autowired
    private FhirParser fhirParser;


    @PostMapping(path = "/fhir/Patient/{id}", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> acceptance(@PathVariable(name = "id") String id, @RequestBody String body) throws FhirValidationException {
        Patient patient = fhirParser.parsePatient(body);
        String operationId = UUID.randomUUID().toString();
        TranslatedInterchange translatedInterchange = fhirToEdifactService.convertToEdifact(patient, operationId, null);
        MeshMessage meshMessage = edifactToMeshMessageService.toMeshMessage(translatedInterchange);
        outboundMeshService.send(meshMessage);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("OperationId", Collections.singletonList(operationId));
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }

}
