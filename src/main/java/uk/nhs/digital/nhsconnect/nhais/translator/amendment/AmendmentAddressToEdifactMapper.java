package uk.nhs.digital.nhsconnect.nhais.translator.amendment;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentAddressToEdifactMapper extends AmendmentToEdifactMapper {

    @Override
    protected List<Segment> mapAllPatches(JsonPatches patches) {
        var personAddress = mapPersonAddress(patches);
        return personAddress
            .<List<Segment>>map(Collections::singletonList)
            .orElse(Collections.emptyList());
    }

    private Optional<PersonAddress> mapPersonAddress(JsonPatches patches) {

        if (!shouldCreateSegment(patches)) {
            return Optional.empty();
        }

        var houseName = patches.getHouseName()
            .map(this::getValue)
            .orElse(null);
        var numberOrRoadName = patches.getNumberOrRoadName()
            .map(this::getValue)
            .orElse(null);
        var locality = patches.getLocality()
            .map(this::getValue)
            .orElse(null);
        var postalTown = patches.getPostTown()
            .map(this::getValue)
            .orElse(null);
        var county = patches.getCounty()
            .map(this::getValue)
            .orElse(null);
        var postalCode = patches.getPostalCode()
            .map(this::getValue)
            .orElse(null);

        var personAddress = PersonAddress.builder()
            .addressLine1(houseName)
            .addressLine2(numberOrRoadName)
            .addressLine3(locality)
            .addressLine4(postalTown)
            .addressLine5(county)
            .postalCode(postalCode)
            .build();

        return Optional.of(personAddress);
    }

    private boolean shouldCreateSegment(JsonPatches patches) {
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
        validatePostalCodePatchIfExists(patches);
        checkIfThereAreAllFiveAddressLinesPatches(patches);
        checkLocalityPostTownAndCountyAllEmptyBlankOrAllNotEmptyBlank(patches);
        checkNoPostTownPatchForRemoveOperation(patches);
    }

    private void validatePostalCodePatchIfExists(JsonPatches patches) {
        if (patches.getPostalCode().isPresent()) {
            validateNonEmptyValues(Collections.singletonList(patches.getPostalCode()));
        }
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
        var localityString = patches.getLocality().get().getValue().get();
        var countyString = patches.getCounty().get().getValue().get();
        var postTownString = patches.getPostTown().get().getValue().get();
        if (!allStringsAreEmptyOrBlank(localityString, countyString, postTownString)
            && !allStringsAreNotEmptyNorBlank(localityString, countyString, postTownString)) {
            throw new FhirValidationException("If at least one of the Address - Locality, Address - Post Town and Address County " +
                "fields is amended for a patient, then the values held for all three of these fields MUST be provided. Actual state: " +
                "Locality: " + localityString + ", Post Town: " + postTownString + ", County: " + countyString);
        }
    }

    private boolean allStringsAreEmptyOrBlank(String localityString, String countyString, String postTownString) {
        return (localityString.isEmpty() || localityString.isBlank())
            && (countyString.isEmpty() || countyString.isBlank())
            && (postTownString.isEmpty() || postTownString.isBlank());
    }

    private boolean allStringsAreNotEmptyNorBlank(String localityString, String countyString, String postTownString) {
        return !localityString.isEmpty() && !localityString.isBlank()
            && !countyString.isEmpty() && !countyString.isBlank()
            && !postTownString.isEmpty() && !postTownString.isBlank();
    }

    private void checkNoPostTownPatchForRemoveOperation(JsonPatches patches) {
        if (patches.getPostTown().isPresent() && patches.getPostTown().get().getOp() == AmendmentPatchOperation.REMOVE) {
            throw new FhirValidationException("Post town ('address/0/line/3') cannot be removed");
        }
    }
}
