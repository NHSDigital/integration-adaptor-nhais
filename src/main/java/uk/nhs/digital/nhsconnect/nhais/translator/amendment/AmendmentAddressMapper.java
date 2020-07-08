package uk.nhs.digital.nhsconnect.nhais.translator.amendment;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.nhais.exceptions.AmendmentValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentAddressMapper extends AmendmentToEdifactMapper{

    @Override
    protected List<Segment> mapAllPatches(JsonPatches patches) {
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
        if (patch != null && !patch.getValue().get().isEmpty() && !patch.getValue().get().isBlank()) {
            return patch.getOp() == AmendmentPatchOperation.REMOVE ? "%" : patch.getValue().get();
        } else {
            throw new AmendmentValidationException("Patch cannot be null nor empty nor blank");
        }
    }

    protected void validatePatches(JsonPatches patches) {
        checkNoPostTownPatchForRemoveOperation(patches);
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

    private void checkNoPostTownPatchForRemoveOperation(JsonPatches patches) {
        if (patches.getPostTown().isPresent() && patches.getPostTown().get().getOp() == AmendmentPatchOperation.REMOVE) {
            throw new AmendmentValidationException("Post town ('address/0/line/3') cannot be removed");
        }
    }
}
