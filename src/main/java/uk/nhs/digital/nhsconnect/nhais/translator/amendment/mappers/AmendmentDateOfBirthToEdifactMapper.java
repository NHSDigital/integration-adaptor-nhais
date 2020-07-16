package uk.nhs.digital.nhsconnect.nhais.translator.amendment.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.exceptions.PatchValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AmendmentDateOfBirthToEdifactMapper extends AmendmentToEdifactMapper {

    @Override
    Segment mapPatches(AmendmentBody amendmentBody) {
        var dateOfBirth = amendmentBody.getJsonPatches().getBirthDate()
            .map(AmendmentPatch::getValue)
            .map(AmendmentValue::get)
            .map(LocalDate::parse)
            .orElseThrow(() -> new PatchValidationException("Missing " + JsonPatches.BIRTH_DATE_PATH));

        return PersonDateOfBirth.builder()
            .dateOfBirth(dateOfBirth)
            .build();
    }

    @Override
    boolean shouldCreateSegment(AmendmentBody amendmentBody) {
        return amendmentBody.getJsonPatches().getBirthDate().isPresent();
    }

    @Override
    void validatePatches(JsonPatches patches) {
        validateNonEmptyValues(List.of(patches.getBirthDate()));

        patches.getBirthDate()
            .filter(AmendmentPatch::isRemoval)
            .ifPresent(value -> {
                throw new PatchValidationException("Illegal remove operation on " + JsonPatches.BIRTH_DATE_PATH);
            });
    }
}
