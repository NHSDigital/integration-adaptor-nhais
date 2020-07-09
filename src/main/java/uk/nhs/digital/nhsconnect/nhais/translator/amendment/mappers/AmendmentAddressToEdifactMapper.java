package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentAddressToEdifactMapper extends AmendmentToEdifactMapper {

    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var patches = amendmentBody.getJsonPatches();

        var houseName = patches.getHouseName()
            .map(AmendmentPatch::getNullableFormattedSimpleValue)
            .orElse(null);
        var numberOrRoadName = patches.getNumberOrRoadName()
            .map(AmendmentPatch::getNullableFormattedSimpleValue)
            .orElse(null);
        var locality = patches.getLocality()
            .map(AmendmentPatch::getNullableFormattedSimpleValue)
            .orElse(null);
        var postalTown = patches.getPostTown()
            .map(AmendmentPatch::getNullableFormattedSimpleValue)
            .orElse(null);
        var county = patches.getCounty()
            .map(AmendmentPatch::getNullableFormattedSimpleValue)
            .orElse(null);
        var postalCode = patches.getPostalCode()
            .map(AmendmentPatch::getNullableFormattedSimpleValue)
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
        checkLocalityPostTownAndCountyAllEmptyBlankOrAllNotEmptyBlank(patches);
    }

    private void checkIfThereAreAllFiveAddressLinesPatches(JsonPatches patches) {
        if(!Stream.of(
            patches.getHouseName(),
            patches.getNumberOrRoadName(),
            patches.getLocality(),
            patches.getPostTown(),
            patches.getCounty())
            .allMatch(Optional::isPresent)) {
            throw new FhirValidationException("All five address lines must be provided for amendment");
        }
    }

    private void checkLocalityPostTownAndCountyAllEmptyBlankOrAllNotEmptyBlank(JsonPatches patches) {
        if(hasLocalityOrCountyRemovalOperation(patches)) {
            return;
        }

        var localityAmendmentPatch = patches.getLocality().get().getValue();
        var countyAmendmentPatch = patches.getCounty().get().getValue();
        var postTownAmendmentPatch = patches.getPostTown().get().getValue();

        var localityString = localityAmendmentPatch == null ? "null" : localityAmendmentPatch.get();
        var countyString = countyAmendmentPatch == null ? "null" : countyAmendmentPatch.get();
        var postTownString = postTownAmendmentPatch == null ? "null" : postTownAmendmentPatch.get();

        if(!allValuesAreNull(localityAmendmentPatch, countyAmendmentPatch, postTownAmendmentPatch)
            && !allValuesAreNotNull(localityAmendmentPatch, countyAmendmentPatch, postTownAmendmentPatch)) {
            throw new FhirValidationException("If at least one of the Address - Locality, Address - Post Town and Address County " +
                "fields is amended for a patient, then the values held for all three of these fields MUST be provided. Actual state: " +
                "Locality: " + localityString + ", Post Town: " + postTownString + ", County: " + countyString);
        }
    }

    private boolean allValuesAreNull(AmendmentValue locality, AmendmentValue county, AmendmentValue postTown) {
        return locality == null && county == null && postTown == null;
    }

    private boolean allValuesAreNotNull(AmendmentValue locality, AmendmentValue county, AmendmentValue postTown) {
        return locality != null && county != null && postTown != null;
    }

    private boolean hasLocalityOrCountyRemovalOperation(JsonPatches patches) {
        return patches.getLocality().get().isRemoval()
        || patches.getCounty().get().isRemoval();
    }

    private void checkNoPostTownPatchForRemoveOperation(JsonPatches patches) {
        if (patches.getPostTown().isPresent() && patches.getPostTown().get().getOp() == AmendmentPatchOperation.REMOVE) {
            throw new FhirValidationException("Post town ('address/0/line/3') cannot be removed");
        }
    }
}
