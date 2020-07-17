package uk.nhs.digital.nhsconnect.nhais.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
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
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith({ SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class RemovalIntegrationTest {
    public static final String URL = "/fhir/Patient/$nhais.removal";

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:outbound_uat_data/removal/app-j-1.fhir.json")
    private Resource removal;

    @Value("classpath:controllerTestsResources/removalBlankNhsNumber.fhir.json")
    private Resource removalWithBlankNhsNumber;

    @Autowired
    private FhirParser fhirParser;

    private Parameters parameters;
    private ParametersExtension parametersExtension;

    @BeforeEach
    private void beforeEach() throws IOException {
        String requestBody = new String(Files.readAllBytes(removal.getFile().toPath()));
        parameters = fhirParser.parseParameters(requestBody);
        parametersExtension = new ParametersExtension(parameters);
    }

    @Test
    void whenNoNhsNumber_thenRespond400() throws Exception {
        Patient patient = parametersExtension.extractPatient();
        patient.getIdentifier().clear();
        String requestBody = fhirParser.encodeToString(parameters);
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
    }

    @Test
    void whenNhsNumberIsBlank_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithBlankNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
    }

    @Test
    void whenNoFreeText_thenRespond400() throws Exception {
        parameters.getParameter().removeIf(p -> p.getName().equals(ParameterNames.FREE_TEXT));
        String requestBody = fhirParser.encodeToString(parameters);
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value freeText is missing in FHIR Parameters");
    }

    @Test
    void whenFreeTextIsBlank_thenRespond400() throws Exception {
        StringType stringType = new StringType(" ");
        parameters.getParameter().get(1).setValue(stringType);
        String requestBody = fhirParser.encodeToString(parameters);
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value freeText is missing in FHIR Parameters");
    }

    @Test
    void whenNoGpTradingPartnerCode_thenRespond400() throws Exception {
        parameters.getParameter().removeIf(p -> p.getName().equals(ParameterNames.GP_TRADING_PARTNER_CODE));
        String requestBody = fhirParser.encodeToString(parameters);
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value gpTradingPartnerCode is missing in FHIR Parameters");
    }

    @Test
    void whenBlankGpTradingPartnerCode_thenRespond400() throws Exception {
        StringType stringType = new StringType(" ");
        parameters.getParameter().get(0).setValue(stringType);
        String requestBody = fhirParser.encodeToString(parameters);
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value gpTradingPartnerCode is missing in FHIR Parameters");
    }



}
