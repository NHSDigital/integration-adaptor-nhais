package uk.nhs.digital.nhsconnect.nhais.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import uk.nhs.digital.nhsconnect.nhais.exceptions.AmendmentValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.service.JsonPatchToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.service.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.utils.HttpHeaders;

import java.util.Collections;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentController {

    private final JsonPatchToEdifactService jsonPatchToEdifactService;
    private final OutboundQueueService outboundQueueService;

    @PatchMapping(path= "/fhir/Patient/{nhsNumber}", consumes="application/json", produces="application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> amendment(@PathVariable(name= "nhsNumber") String nhsNumber, @RequestBody AmendmentBody amendmentBody) {
        LOGGER.info("Amendment request: {}", amendmentBody);
        if (!nhsNumber.equals(amendmentBody.getNhsNumber())) {
            throw new AmendmentValidationException("Request body has different NHS number than provided in request path");
        }
        OutboundMeshMessage meshMessage = jsonPatchToEdifactService.convertToEdifact(amendmentBody);
        outboundQueueService.publish(meshMessage);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(HttpHeaders.OPERATION_ID, Collections.singletonList(meshMessage.getOperationId()));
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }

}
