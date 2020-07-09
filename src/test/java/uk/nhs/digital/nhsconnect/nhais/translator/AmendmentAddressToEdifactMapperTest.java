package uk.nhs.digital.nhsconnect.nhais.translator;

import static uk.nhs.digital.nhsconnect.nhais.translator.AmendmentFhirToEdifactTestBase.REMOVE_INDICATOR;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.AmendmentAddressToEdifactMapper;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({ MockitoExtension.class, SoftAssertionsExtension.class })
public class AmendmentAddressToEdifactMapperTest {

    private static final String NHS_NUMBER = "1234";
    private static final String HOUSE_NAME = "FLAT 49";
    private static final String ROAD_NAME = "23 JACKSON SQUARE";
    private static final String LOCALITY = "ST PAULS CRAY";
    private static final String POST_TOWN = "ORPINGTON";
    private static final String COUNTY = "KENT";

    private final AmendmentAddressToEdifactMapper translator = new AmendmentAddressToEdifactMapper();

    @Mock
    private AmendmentBody amendmentBody;

    @Mock
    private JsonPatches jsonPatches;

    @BeforeEach
    void setUp() {
        reset(amendmentBody, jsonPatches);

        lenient().when(amendmentBody.getNhsNumber()).thenReturn(NHS_NUMBER);
        lenient().when(jsonPatches.getAmendmentBody()).thenReturn(amendmentBody);
        when(amendmentBody.getJsonPatches()).thenReturn(jsonPatches);
    }

    @Test
    void whenReplacingAllFiveAddressLinesFields_expectAllAddressLinesAreMapped() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(HOUSE_NAME))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(ROAD_NAME))));
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(LOCALITY))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(POST_TOWN))));
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(COUNTY))));

        List<Segment> segments = translator.map(amendmentBody);

        assertThat(segments).usingFieldByFieldElementComparator()
            .containsExactly(PersonAddress.builder()
                .addressLine1(HOUSE_NAME)
                .addressLine2(ROAD_NAME)
                .addressLine3(LOCALITY)
                .addressLine4(POST_TOWN)
                .addressLine5(COUNTY)
                .build());
    }

    @Test
    void whenRemovingFourAddressLinesFields_expectFourAddressLinesRemoved() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REMOVE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(REMOVE_INDICATOR))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(REMOVE_INDICATOR))));
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(REMOVE_INDICATOR))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.empty());
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(REMOVE_INDICATOR))));

        List<Segment> segments = translator.map(amendmentBody);

        assertThat(segments).usingFieldByFieldElementComparator()
            .containsExactly(PersonAddress.builder()
                .addressLine1(REMOVE_INDICATOR)
                .addressLine2(REMOVE_INDICATOR)
                .addressLine3(REMOVE_INDICATOR)
                .addressLine4(null)
                .addressLine5(REMOVE_INDICATOR)
                .build());
    }
}
