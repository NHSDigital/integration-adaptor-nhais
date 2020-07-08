package uk.nhs.digital.nhsconnect.nhais.translator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.exceptions.AmendmentValidationException;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentAddressTranslator implements AmendmentToEdifactTranslator {

    @Override
    public List<Segment> translate(AmendmentBody amendmentBody) throws FhirValidationException {
        var patches = amendmentBody.getJsonPatches();
        validatePatches(patches);
        return mapAllPatches(patches);
    }

    private List<Segment> mapAllPatches(JsonPatches patches) {
        var personAddress = mapPersonAddress(patches);
        return personAddress
            .<List<Segment>>map(Collections::singletonList)
            .orElse(Collections.emptyList());
    }

    private Optional<PersonAddress> mapPersonAddress(JsonPatches patches) {
        if (shouldCreatePersonAddressSegment(patches)) {
            var county = patches.getCounty()
                .map(this::getPatchValue)
                .orElse(null);
            var houseName = patches.getHouseName()
                .map(this::getPatchValue)
                .orElse(null);
            var houseNumber = patches.getNumberOrRoadName()
                .map(this::getPatchValue)
                .orElse(null);
            var postalCode = patches.getPostalCode()
                .map(this::getPatchValue)
                .orElse(null);
            var postalTown = patches.getPostTown()
                .map(this::getPatchValue)
                .orElse(null);

            var personAddress = PersonAddress.builder()
                .addressLine1(houseName)
                .addressLine2(houseNumber)
                .addressLine3(postalTown)
                .addressLine4(county)
                .postalCode(postalCode)
                .build();

            return Optional.of(personAddress);
        }
        return Optional.empty();
    }

    private boolean shouldCreatePersonAddressSegment(JsonPatches patches) {
        return Stream.of(
            patches.getHouseName(),
            patches.getNumberOrRoadName(),
            patches.getPostTown(),
            patches.getCounty(),
            patches.getPostalCode())
            .anyMatch(Optional::isPresent);
    }

    private String getPatchValue(AmendmentPatch patch) {
        return patch.getOp() == AmendmentPatchOperation.REMOVE ? Strings.EMPTY : patch.getValue().get();
    }

    private void validatePatches(JsonPatches patches) {
        if (!isAllFiveAddressLinesNotEmpty(patches) && !isAllFiveAddressLinesEmpty(patches)) {
            throw new AmendmentValidationException("All five lines of address needs to be update");
        }
    }

    private boolean isAllFiveAddressLinesNotEmpty(JsonPatches patches) {
        return patches.getHouseName().isPresent() ||
            patches.getNumberOrRoadName().isPresent() &&
            patches.getLocality().isPresent() &&
            patches.getPostTown().isPresent() &&
            patches.getCounty().isPresent();
    }

    private boolean isAllFiveAddressLinesEmpty(JsonPatches patches) {
        return patches.getHouseName().isEmpty()||
            patches.getNumberOrRoadName().isEmpty() &&
                patches.getLocality().isEmpty() &&
                patches.getPostTown().isEmpty() &&
                patches.getCounty().isEmpty();
    }
}
