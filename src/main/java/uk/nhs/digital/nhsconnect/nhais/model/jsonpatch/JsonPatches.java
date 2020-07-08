package uk.nhs.digital.nhsconnect.nhais.model.jsonpatch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JsonPatches {

    public static final String ALL_FORENAMES_PATH = "/name/0/given";
    private static final String TITLE_PATH = "/name/0/prefix/0";
    private static final String SURNAME_PATH = "/name/0/family";
    private static final String PREVIOUS_SURNAME_PATH = "/name/1/family";
    private static final String FIRST_FORENAME_PATH = "/name/0/given/0";
    private static final String SECOND_FORENAME_PATH = "/name/0/given/1";
    private static final String OTHER_FORENAMES_PATH = "/name/0/given/2";
    private static final String SEX_PATH = "/gender";
    private static final String BIRTH_DATE_PATH = "/birthDate";
    private static final String HOUSE_NAME_PATH = "/address/0/line/0";
    private static final String NUMBER_OR_ROAD_NAME_PATH = "/address/0/line/1";
    private static final String LOCALITY = "/address/0/line/2";
    private static final String POST_TOWN_PATH = "/address/0/line/3";
    private static final String COUNTY_PATH = "/address/0/line/4";
    private static final String POSTAL_CODE_PATH = "/address/0/postalCode";

    @Getter
    private final AmendmentBody amendmentBody;
    private final List<AmendmentPatch> patches;

    public Optional<AmendmentPatch> getTitle() {
        return filterSimpleValues(TITLE_PATH);
    }

    public Optional<AmendmentPatch> getSurname() {
        return filterSimpleValues(SURNAME_PATH);
    }

    public Optional<AmendmentPatch> getPreviousSurname() {
        return filterSimpleValues(PREVIOUS_SURNAME_PATH);
    }

    public Optional<AmendmentPatch> getFirstForename() {
        return filterSimpleValues(FIRST_FORENAME_PATH);
    }

    public Optional<AmendmentPatch> getSecondForename() {
        return filterSimpleValues(SECOND_FORENAME_PATH);
    }

    public Optional<AmendmentPatch> getOtherForenames() {
        return filterSimpleValues(OTHER_FORENAMES_PATH);
    }

    public Optional<AmendmentPatch> getAllForenamesPath() {
        return filterSimpleValues(ALL_FORENAMES_PATH);
    }

    public Optional<AmendmentPatch> getSex() {
        return filterSimpleValues(SEX_PATH);
    }

    public Optional<AmendmentPatch> getBirthDate() {
        return filterSimpleValues(BIRTH_DATE_PATH);
    }

    public Optional<AmendmentPatch> getHouseName() {
        return filterSimpleValues(HOUSE_NAME_PATH);
    }

    public Optional<AmendmentPatch> getNumberOrRoadName(){
        return filterSimpleValues(NUMBER_OR_ROAD_NAME_PATH);
    }

    public Optional<AmendmentPatch> getLocality() {
        return filterSimpleValues(LOCALITY);
    }

    public Optional<AmendmentPatch> getPostTown() {
        return filterSimpleValues(POST_TOWN_PATH);
    }

    public Optional<AmendmentPatch> getCounty() {
        return filterSimpleValues(COUNTY_PATH);
    }

    public Optional<AmendmentPatch> getPostalCode() {
        return filterSimpleValues(POSTAL_CODE_PATH);
    }

    private Optional<AmendmentPatch> filterSimpleValues(String path) {
        return patches.stream()
            .filter(patch -> path.equalsIgnoreCase(patch.getPath()))
            .findFirst();
    }

    public <T extends AmendmentExtension> Optional<AmendmentPatch> getExtension(Class<T> clazz) {
        return patches.stream()
            .filter(patch -> patch.getValue() instanceof AmendmentExtension)
            .filter(patch -> clazz.isAssignableFrom(patch.getValue().getClass()))
            .findFirst();
    }

    public Optional<AmendmentPatch> getDrugsDispensedMarker() {
        return getExtension(AmendmentExtension.DrugsDispensedMarker.class);
    }

    public Optional<AmendmentPatch> getBirthplace() {
        return getExtension(AmendmentExtension.Birthplace.class);
    }

    public Optional<AmendmentPatch> getPreviousGp() {
        return getExtension(AmendmentExtension.PreviousGp.class);
    }

    public Optional<AmendmentPatch> getResidentialInstituteCode() {
        return getExtension(AmendmentExtension.ResidentialInstituteCode.class);
    }
}
