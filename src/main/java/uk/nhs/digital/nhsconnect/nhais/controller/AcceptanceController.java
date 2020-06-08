package uk.nhs.digital.nhsconnect.nhais.controller;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mapper.PersonAddressMapper;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
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

    @Autowired
    private FhirParser fhirParser;

    @PostMapping(path = "/fhir/Patient", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> acceptance(@RequestBody String body) throws FhirValidationException, EdifactValidationException {
//        Patient patient = fhirParser.parsePatient(body);
//        TranslatedInterchange translatedInterchange = fhirToEdifactService.convertToEdifact(patient, ReferenceTransactionType.TransactionType.ACCEPTANCE);
//        MeshMessage meshMessage = edifactToMeshMessageService.toMeshMessage(translatedInterchange);
//        outboundMeshService.send(meshMessage);
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.put(HttpHeaders.OPERATION_ID, Collections.singletonList(translatedInterchange.getOperationId()));
//        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);

        System.out.println(body);
        Parameters parameters = fhirParser.parseParameters(body);
        PersonAddressMapper personAddressMapper = new PersonAddressMapper();

        System.out.println(personAddressMapper.map(parameters).toEdifact());


        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
