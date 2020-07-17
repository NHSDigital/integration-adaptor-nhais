package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.OperationOutcome;
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
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({ SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class RemovalIntegrationTest {
    public static final String URL = "/fhir/Patient/$nhais.removal";

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:controllerTestsResources/removalBlankFreeText.fhir.json")
    private Resource removalWithBlankFreeText;

    @Value("classpath:controllerTestsResources/removalBlankGpTradingPartnerCode.fhir.json")
    private Resource removalWithBlankGpTradingPartnerCode;

    @Value("classpath:controllerTestsResources/removalBlankNhsNumber.fhir.json")
    private Resource removalWithBlankNhsNumber;

    @Value("classpath:controllerTestsResources/removalEmptyFreeText.fhir.json")
    private Resource removalWithEmptyFreeText;

    @Value("classpath:controllerTestsResources/removalEmptyGpTradingPartnerCode.fhir.json")
    private Resource removalWithEmptyGpTradingPartnerCode;

    @Value("classpath:controllerTestsResources/removalEmptyNhsNumber.fhir.json")
    private Resource removalWithEmptyNhsNumber;

    @Value("classpath:controllerTestsResources/removalNoFreeText.fhir.json")
    private Resource removalWithNoFreeText;

    @Value("classpath:controllerTestsResources/removalNoGpTradingPartnerCode.fhir.json")
    private Resource removalWithNoGpTradingPartnerCode;

    @Value("classpath:controllerTestsResources/removalNoNhsNumber.fhir.json")
    private Resource removalWithNoNhsNumber;

    @Value("classpath:controllerTestsResources/removalNullFreeText.fhir.json")
    private Resource removalWithNullFreeText;

    @Value("classpath:controllerTestsResources/removalNullGpTradingPartnerCode.fhir.json")
    private Resource removalWithNullGpTradingPartnerCode;

    @Value("classpath:controllerTestsResources/removalNullNhsNumber.fhir.json")
    private Resource removalWithNullNhsNumber;

    @Autowired
    private FhirParser fhirParser;

    @Test
    void whenBlankNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithBlankNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
    }


    @Test
    void whenEmptyNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithEmptyNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters: Invalid attribute value \"\": Attribute values must not be empty (\"\")");
    }


    @Test
    void whenNoNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithNoNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
    }

    @Test
    void whenNullNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithNullNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
    }

    @Test
    void whenBlankFreeText_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithBlankFreeText.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("FTX: Attribute freeTextValue is blank or missing");
    }

    @Test
    void whenEmptyFreeText_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithEmptyFreeText.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters");
    }

    @Test
    void whenNoFreeText_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithNoFreeText.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value freeText is missing in FHIR Parameters");
    }

    @Test
    void whenNullFreeText_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithNullFreeText.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value freeText is missing in FHIR Parameters");
    }

    @Test
    void whenBlankGpTradingPartnerCode_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithBlankGpTradingPartnerCode.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value gpTradingPartnerCode is blank or missing in FHIR Parameters");
    }


    @Test
    void whenEmptyGpTradingPartnerCode_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithEmptyGpTradingPartnerCode.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters");
    }

    @Test
    void whenNoGpTradingPartnerCode_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithNoGpTradingPartnerCode.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value gpTradingPartnerCode is blank or missing in FHIR Parameters");
    }

    @Test
    void whenNullGpTradingPartnerCode_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(removalWithNullGpTradingPartnerCode.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Value gpTradingPartnerCode is blank or missing in FHIR Parameters");
    }


}
