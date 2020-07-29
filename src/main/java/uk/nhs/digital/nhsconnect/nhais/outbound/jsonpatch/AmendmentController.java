package uk.nhs.digital.nhsconnect.nhais.outbound.jsonpatch;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.OutboundMeshMessage;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentStringExtension;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.outbound.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.utils.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentController {

    private final JsonPatchToEdifactService jsonPatchToEdifactService;
    private final OutboundQueueService outboundQueueService;
    private final ObjectMapper objectMapper;

    @PatchMapping(path = "/fhir/Patient/{nhsNumber}", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> amendment(@PathVariable(name = "nhsNumber") String nhsNumber, @RequestBody String body) {
        AmendmentBody amendmentBody = parseRequest(body);
        LOGGER.info("Amendment request: {}", amendmentBody);
        validateRequest(nhsNumber, amendmentBody);
        OutboundMeshMessage meshMessage = jsonPatchToEdifactService.convertToEdifact(amendmentBody);
        outboundQueueService.publish(meshMessage);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(HttpHeaders.OPERATION_ID, List.of(meshMessage.getOperationId()));
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }

    private AmendmentBody parseRequest(@RequestBody String body) {
        try {
            JsonParser parser = objectMapper.createParser(body);
            return parser.readValueAs(AmendmentBody.class);
        } catch (IOException ex) {
            throw new AmendmentValidationException(ex.getMessage());
        }
    }

    private void validateRequest(String nhsNumber, AmendmentBody amendmentBody) {
        validateRequiredFields(amendmentBody);

        if (!nhsNumber.equals(amendmentBody.getNhsNumber())) {
            throw new AmendmentValidationException("Request body has different NHS number than provided in request path");
        }

        validateDuplicatedPaths(amendmentBody.getPatches());
        validateDuplicatedExtensions(amendmentBody.getPatches());
    }

    private void validateDuplicatedPaths(List<AmendmentPatch> patches) {
        var amendmentPaths = patches.stream()
            .filter(AmendmentPatch::isNotExtension)
            .map(AmendmentPatch::getPath)
            .collect(Collectors.toSet());

        var patchesWithoutExtensions = patches.stream()
            .filter(AmendmentPatch::isNotExtension)
            .collect(Collectors.toSet());

        if (patchesWithoutExtensions.size() != amendmentPaths.size()) {
            throw new AmendmentValidationException("Request contains path that is used multiple times. Each patch path must only be used once within the amendment request");
        }
    }

    private void validateDuplicatedExtensions(List<AmendmentPatch> patches) {
        var extensionTypes = patches.stream()
            .filter(AmendmentPatch::isExtension)
            .map(AmendmentPatch::getAmendmentValue)
            .map(AmendmentValue::getClass)
            .collect(Collectors.toSet());

        var allExtensionPatches = patches.stream()
            .filter(AmendmentPatch::isExtension)
            .collect(Collectors.toList());
        if (allExtensionPatches.size() != extensionTypes.size()) {
            throw new AmendmentValidationException("Request contains extension that is used multiple times. Each extension patch must only be used once within the amendment request");
        }
    }

    private void validateRequiredFields(AmendmentBody amendmentBody) {
        if (StringUtils.isBlank(amendmentBody.getNhsNumber())) {
            throw new AmendmentValidationException("NHS number is missing in request body");
        }
        if (StringUtils.isBlank(amendmentBody.getGpCode())) {
            throw new AmendmentValidationException("Existing GP Code is missing in request body");
        }
        if (StringUtils.isBlank(amendmentBody.getHealthcarePartyCode())) {
            throw new AmendmentValidationException("Healthcare authority party code is missing in request body");
        }
        if (StringUtils.isBlank(amendmentBody.getGpTradingPartnerCode())) {
            throw new AmendmentValidationException("GP Trading Partner Code is missing in request body");
        }
        if (CollectionUtils.isEmpty(amendmentBody.getPatches())) {
            throw new AmendmentValidationException("Request body has to contain at least one patch operation");
        }

        validateNonEmptyValues(amendmentBody.getPatches());
    }

    private void validateNonEmptyValues(List<AmendmentPatch> amendmentPatches) {
        var simpleValuesInvalidPaths = amendmentPatches.stream()
            .filter(amendmentPatch -> amendmentPatch.getAmendmentValue() != null)
            .filter(AmendmentPatch::isNotExtension)
            .filter(amendmentPatch -> StringUtils.isBlank(amendmentPatch.getFormattedSimpleValue()))
            .map(AmendmentPatch::getPath);

        var extensionValuesInvalidPaths = amendmentPatches.stream()
            .filter(amendmentPatch -> amendmentPatch.getAmendmentValue() != null)
            .filter(AmendmentPatch::isExtension)
            .filter(amendmentPatch -> {
                if (amendmentPatch.getAmendmentValue() instanceof AmendmentStringExtension) {
                    return StringUtils.EMPTY.equals(amendmentPatch.getAmendmentValue().get());
                }
                return amendmentPatch.getAmendmentValue().get() == null;
            })
            .map(AmendmentPatch::getPath);

        List<String> invalidAmendmentPaths = Stream.of(
            simpleValuesInvalidPaths, extensionValuesInvalidPaths)
            .flatMap(Function.identity())
            .collect(Collectors.toList());

        if (invalidAmendmentPaths.size() > 0) {
            throw new PatchValidationException("Missing/empty values for: " + invalidAmendmentPaths);
        }
    }

}
