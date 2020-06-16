package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import java.util.Collections;

public class FhirPayloadTest {

    @Test
    public void acceptance() {
        //TODO in progress
        Patient patient = new Patient();
        HumanName humanName = new HumanName();
        humanName.setFamily("Surname");
        patient.setName(Collections.singletonList(humanName));
        FhirParser fhirParser = new FhirParser();
        System.out.println(fhirParser.encodeToString(patient));


        // {"resourceType":"Patient","name":[{"family":"Surname"}]}

        // PNA+PAT+N/10/10:OPI+++SU:STEVENS+FO:CHARLES+TI:MR+MI:ANTHONY+FS:JOHN'
        // PNA Person Name
        // PAT Patient (current/usual name)
        // N/10/10:OPI   N/10/10 NHS Number - but in the old format, current NHS number are 10 numerical digits?   :OPI is the "data type"
        // Two empty fields: Name Type, Name Status - Always unused
        // Rest of fields: surname, forename, title, middle name, third (and other, space separated) names

    }

}
