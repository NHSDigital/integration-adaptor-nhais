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
        if (checkIfThereAreAllFiveAddressLinesPatches(patches)) {
            checkNoPostTownPatchForRemoveOperation(patches);
        }
    }

    private void validatePostalCodePatchIfExists(JsonPatches patches) {
        if (patches.getPostalCode().isPresent()) {
            validateNonEmptyValues(Collections.singletonList(patches.getPostalCode()));
        }
    }

    private boolean checkIfThereAreAllFiveAddressLinesPatches(JsonPatches patches) {
        return Stream.of(
            patches.getHouseName(),
            patches.getNumberOrRoadName(),
            patches.getLocality(),
            patches.getPostTown(),
            patches.getCounty())
            .allMatch(Optional::isPresent);
    }

    private void checkNoPostTownPatchForRemoveOperation(JsonPatches patches) {
        if (patches.getPostTown().isPresent() && patches.getPostTown().get().getOp() == AmendmentPatchOperation.REMOVE) {
            throw new FhirValidationException("Post town ('address/0/line/3') cannot be removed");
        }
    }

}
