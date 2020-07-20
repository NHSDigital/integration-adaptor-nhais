package uk.nhs.digital.nhsconnect.nhais.model.fhir;

/**
 * JSON path / pointer values of some Patient resource elements supported by the adaptor. Used to create validation error
 * messages. These items are generally not the amendable ones.
 *
 * @see uk.nhs.digital.nhsconnect.nhais.model.jsonpatch.JsonPatches for paths of amendable items
 */
public class PatientJsonPaths {
    public static final String NHS_NUMBER_PATH = "/identifier/0/value";
}
