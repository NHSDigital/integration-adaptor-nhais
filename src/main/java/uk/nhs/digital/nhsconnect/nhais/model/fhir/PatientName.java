package uk.nhs.digital.nhsconnect.nhais.model.fhir;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import lombok.Builder;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.StringType;

@DatatypeDef(name="HumanName")
@Builder
public class PatientName extends HumanName {

    private final String familyName;
    private final String forename;
    private final String title;
    private final String middleName;
    private final String thirdForename;

    public PatientName(String familyName, String forename, String title, String middleName, String thirdForename) {
        this.familyName = familyName;
        this.forename = forename;
        this.title = title;
        this.middleName = middleName;
        this.thirdForename = thirdForename;
        this.setFamily(familyName);
        this.setGiven(Stream.of(forename, middleName, thirdForename)
            .filter(StringUtils::isNotBlank)
            .map(StringType::new)
            .collect(Collectors.toList())
        );
        this.setPrefix(Stream.of(title)
            .filter(StringUtils::isNotBlank)
            .map(StringType::new)
            .collect(Collectors.toList())
        );
    }
}
