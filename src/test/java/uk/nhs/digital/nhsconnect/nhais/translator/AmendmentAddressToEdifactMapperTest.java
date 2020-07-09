package uk.nhs.digital.nhsconnect.nhais.translator;

import static uk.nhs.digital.nhsconnect.nhais.translator.AmendmentFhirToEdifactTestBase.REMOVE_INDICATOR;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentBody;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatch;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentPatchOperation;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.AmendmentValue;
import uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches;
import uk.nhs.digital.nhsconnect.nhais.translator.amendment.AmendmentAddressToEdifactMapper;

import org.apache.logging.log4j.util.Strings;
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

    private static final String LOCALITY_POST_TOWN_AND_LOCALITY_INCONSISTENCY_MESSAGE = "If at least one of the Address - Locality, Address - Post Town and Address County " +
        "fields is amended for a patient, then the values held for all three of these fields MUST be provided. Actual state: ";
    private static final String ALL_FIVE_ADDRESS_LINES_NEEDED_MESSAGE = "All five address lines must be provided for amendment";

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
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(AmendmentPatchOperation.REPLACE).setValue(AmendmentValue.from(POST_TOWN))));
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(REMOVE_INDICATOR))));

        List<Segment> segments = translator.map(amendmentBody);

        assertThat(segments).usingFieldByFieldElementComparator()
            .containsExactly(PersonAddress.builder()
                .addressLine1(REMOVE_INDICATOR)
                .addressLine2(REMOVE_INDICATOR)
                .addressLine3(REMOVE_INDICATOR)
                .addressLine4(POST_TOWN)
                .addressLine5(REMOVE_INDICATOR)
                .build());
    }

    @Test
    void whenAmendingLocality_andPostTownAndCountyIsEmpty_thenThrowsFhirValidationException() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(HOUSE_NAME))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(ROAD_NAME))));
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(LOCALITY))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(Strings.EMPTY))));
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(Strings.EMPTY))));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage(LOCALITY_POST_TOWN_AND_LOCALITY_INCONSISTENCY_MESSAGE
                + "Locality: " + LOCALITY + ", Post Town: " + Strings.EMPTY + ", County: " + Strings.EMPTY);
    }

    @Test
    void whenAmendingPostTown_andLocalityAndCountyIsEmpty_thenThrowsFhirValidationException() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(HOUSE_NAME))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(ROAD_NAME))));
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(Strings.EMPTY))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(POST_TOWN))));
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(Strings.EMPTY))));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage(LOCALITY_POST_TOWN_AND_LOCALITY_INCONSISTENCY_MESSAGE +
                "Locality: " + Strings.EMPTY + ", Post Town: " + POST_TOWN + ", County: " + Strings.EMPTY);
    }

    @Test
    void whenAmendingCounty_andLocalityAndPostTownIsEmpty_thenThrowsFhirValidationException() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(HOUSE_NAME))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(ROAD_NAME))));
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(Strings.EMPTY))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(Strings.EMPTY))));
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(COUNTY))));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage(LOCALITY_POST_TOWN_AND_LOCALITY_INCONSISTENCY_MESSAGE +
                "Locality: " + Strings.EMPTY + ", Post Town: " + Strings.EMPTY + ", County: " + COUNTY);
    }

    @Test
    void whenHouseNameMissing_thenThrowsFhirValidationException() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.empty());
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(ROAD_NAME))));
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(LOCALITY))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(POST_TOWN))));
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(COUNTY))));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage(ALL_FIVE_ADDRESS_LINES_NEEDED_MESSAGE);
    }

    @Test
    void whenNumberOrRoadNameMissing_thenThrowsFhirValidationException() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(HOUSE_NAME))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.empty());
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(LOCALITY))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(POST_TOWN))));
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(COUNTY))));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage(ALL_FIVE_ADDRESS_LINES_NEEDED_MESSAGE);
    }

    @Test
    void whenLocalityMissing_thenThrowsFhirValidationException() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(HOUSE_NAME))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(ROAD_NAME))));
        when(jsonPatches.getLocality()).thenReturn(Optional.empty());
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(POST_TOWN))));
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(COUNTY))));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage(ALL_FIVE_ADDRESS_LINES_NEEDED_MESSAGE);
    }

    @Test
    void whenPostTownMissing_thenThrowsFhirValidationException() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(HOUSE_NAME))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(ROAD_NAME))));
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(LOCALITY))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.empty());
        when(jsonPatches.getCounty()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(COUNTY))));

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage(ALL_FIVE_ADDRESS_LINES_NEEDED_MESSAGE);
    }

    @Test
    void whenCountyMissing_thenThrowsFhirValidationException() {
        AmendmentPatchOperation operation = AmendmentPatchOperation.REPLACE;
        when(jsonPatches.getHouseName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(HOUSE_NAME))));
        when(jsonPatches.getNumberOrRoadName()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(ROAD_NAME))));
        when(jsonPatches.getLocality()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(LOCALITY))));
        when(jsonPatches.getPostTown()).thenReturn(Optional.of(new AmendmentPatch()
            .setOp(operation).setValue(AmendmentValue.from(POST_TOWN))));
        when(jsonPatches.getCounty()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> translator.map(amendmentBody))
            .isExactlyInstanceOf(FhirValidationException.class)
            .hasMessage(ALL_FIVE_ADDRESS_LINES_NEEDED_MESSAGE);
    }


}
