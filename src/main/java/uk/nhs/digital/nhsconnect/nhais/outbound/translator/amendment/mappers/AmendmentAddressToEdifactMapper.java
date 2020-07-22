package uk.nhs.digital.nhsconnect.nhais.outbound.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.outbound.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.outbound.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentAddressToEdifactMapper extends AmendmentToEdifactMapper {

    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var patches = amendmentBody.getJsonPatches();

        var houseName = patches.getHouseName()
            .map(AmendmentPatch::getNullSafeFormattedSimpleValue)
            .orElse(null);
        var numberOrRoadName = patches.getNumberOrRoadName()
            .map(AmendmentPatch::getNullSafeFormattedSimpleValue)
            .orElse(null);
        var locality = patches.getLocality()
            .map(AmendmentPatch::getNullSafeFormattedSimpleValue)
            .orElse(null);
        var postalTown = patches.getPostTown()
            .map(AmendmentPatch::getNullSafeFormattedSimpleValue)
            .orElse(null);
        var county = patches.getCounty()
            .map(AmendmentPatch::getNullSafeFormattedSimpleValue)
            .orElse(null);
        var postalCode = patches.getPostalCode()
            .map(AmendmentPatch::getNullSafeFormattedSimpleValue)
            .orElse(null);

        return PersonAddress.builder()
            .addressLine1(houseName)
            .addressLine2(numberOrRoadName)
            .addressLine3(locality)
            .addressLine4(postalTown)
            .addressLine5(county)
            .postalCode(postalCode)
            .build();
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        var patches = amendmentBody.getJsonPatches();
        return Stream.of(
            patches.getHouseName(),
            patches.getNumberOrRoadName(),
            patches.getLocality(),
            patches.getPostTown(),
            patches.getCounty(),
            patches.getPostalCode())
            .anyMatch(Optional::isPresent);
    }

    @Override
    protected void validatePatches(JsonPatches patches) {
        checkIfThereAreAllFiveAddressLinesPatches(patches);
        validateNonEmptyValues(List.of(
            patches.getHouseName(),
            patches.getNumberOrRoadName(),
            patches.getLocality(),
            patches.getPostTown(),
            patches.getCounty(),
            patches.getPostalCode()));
        checkNoPostTownPatchForRemoveOperation(patches);
    }

    private void checkIfThereAreAllFiveAddressLinesPatches(JsonPatches patches) {
        if (!Stream.of(
            patches.getHouseName(),
            patches.getNumberOrRoadName(),
            patches.getLocality(),
            patches.getPostTown(),
            patches.getCounty())
            .allMatch(Optional::isPresent)) {
            throw new FhirValidationException("All five address lines must be provided for amendment");
        }
    }

    private void checkNoPostTownPatchForRemoveOperation(JsonPatches patches) {
        if (patches.getPostTown().isPresent() && patches.getPostTown().get().isRemoval()) {
            throw new PatchValidationException("Post town ('address/0/line/3') cannot be removed");
        }
    }
}
