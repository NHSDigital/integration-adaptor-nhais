package uk.nhs.digital.nhsconnect.nhais.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;
import uk.nhs.digital.nhsconnect.nhais.utils.FhirToJson;

import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.Test;

class EdifactToFhirServiceTest {

    private final String exampleMessage = "UNB+UNOA:2+TES5+XX11+020114:1619+00000003'\n" +
        "UNH+00000004+FHSREG:0:1:FH:FHS001'\n" +
        "BGM+++507'\n" +
        "NAD+FHS+XX1:954'\n" +
        "DTM+137:199201141619:203'\n" +
        "RFF+950:G1'\n" +
        "S01+1'\n" +
        "RFF+TN:18'\n" +
        "NAD+GP+2750922,295:900'\n" +
        "NAD+RIC+RT:956'\n" +
        "QTY+951:6'\n" +
        "QTY+952:3'\n" +
        "HEA+ACD+A:ZZZ'\n" +
        "HEA+ATP+2:ZZZ'\n" +
        "HEA+BM+S:ZZZ'\n" +
        "HEA+DM+Y:ZZZ'\n" +
        "DTM+956:19920114:102'\n" +
        "LOC+950+GLASGOW'\n" +
        "FTX+RGI+++BABY AT THE REYNOLDS-THORPE CENTRE'\n" +
        "S02+2'\n" +
        "PNA+PAT++++SU:KENNEDY+FO:SARAH+TI:MISS+MI:ANGELA'\n" +
        "DTM+329:19911209:102'\n" +
        "PDI+2'\n" +
        "NAD+PAT++??:26 FARMSIDE CLOSE:ST PAULS CRAY:ORPINGTON:KENT+++++BR6  7ET'\n" +
        "UNT+24+00000004'\n" +
        "UNZ+1+00000003'";

    @Test
    void convertToFhir() {

        Parameters parameters = new EdifactToFhirService().convertToFhir(new EdifactParser().parse(exampleMessage));

        List<Resource> resources = parameters.getParameter().stream()
            .map(Parameters.ParametersParameterComponent::getResource)
            .collect(Collectors.toList());

        assertThat(resources).hasOnlyElementsOfType(Patient.class);
        assertThat(parameters.getParameterFirstRep().getName()).isEqualTo("patient");
        Patient patient = (Patient) parameters.getParameterFirstRep().getResource();

        assertThat(patient.getManagingOrganization().getResource().getIdElement().getIdPart()).isEqualTo("XX1");
        assertThat(patient.getGeneralPractitionerFirstRep().getResource().getIdElement().getIdPart()).isEqualTo("2750922,295");
//        FhirToJson.printResource(parameters);
    }
}