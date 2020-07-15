package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import java.io.IOException;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class DeductionIntegrationTest {
    private static final String expectedOperationId = "c35b1432682a04ca77b287b11f240fbd8c46f22fe589b513a84e307b57dcb820";
    public static final String URL = "/fhir/Patient/$nhais.deduction";

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:outbound_uat_data/deduction/app-j-1.fhir.json")
    private Resource deduction;

    @Autowired
    private FhirParser fhirParser;

    private Parameters parameters;
    private ParametersExtension parametersExtension;

    @BeforeEach
    private void beforeEach() throws IOException  {
        String requestBody = new String(Files.readAllBytes(deduction.getFile().toPath()));
        parameters = fhirParser.parseParameters(requestBody);
        parametersExtension = new ParametersExtension(parameters);
    }

    @Test
    void whenNoNhsNumber_thenRespond400() throws Exception {
       Patient patient = parametersExtension.extractPatient();
       patient.getIdentifier().clear();
       String requestBody = fhirParser.encodeToString(parameters);
       mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenNoDateOfDeduction_thenRespond400() throws Exception {
        parameters.getParameter().removeIf(p -> p.getName().equals(ParameterNames.DATE_OF_DEDUCTION));
        String requestBody = fhirParser.encodeToString(parameters);
        mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }


}