package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.mapper.TransactionTypeMapper;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.service.FhirToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.service.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.utils.HttpHeaders;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirController {

    private final OutboundQueueService outboundQueueService;

    private final FhirToEdifactService fhirToEdifactService;

    private final FhirParser fhirParser;

    @PostMapping(path = "/fhir/Patient/{transactionTypeParam}", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> acceptance(@PathVariable String transactionTypeParam, @RequestBody String body) throws FhirValidationException {
        Parameters parameters = fhirParser.parseParameters(body);
        ReferenceTransactionType.Outbound transactionType = new TransactionTypeMapper().mapTransactionType(transactionTypeParam);
        OutboundMeshMessage meshMessage = fhirToEdifactService.convertToEdifact(parameters, transactionType);
        outboundQueueService.publish(meshMessage);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(HttpHeaders.OPERATION_ID, List.of(meshMessage.getOperationId()));
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }

}
