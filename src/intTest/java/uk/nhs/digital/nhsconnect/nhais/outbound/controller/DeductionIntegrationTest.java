package uk.nhs.digital.nhsconnect.nhais.outbound.controller;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.PatientJsonPaths;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@DirtiesContext
public class DeductionIntegrationTest {
    public static final String URL = "/fhir/Patient/$nhais.deduction";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FhirParser fhirParser;

    @Value("classpath:controllerTestsResources/deductionBlankNhsNumber.fhir.json")
    private Resource deductionWithBlankNhsNumber;

    @Value("classpath:controllerTestsResources/deductionEmptyNhsNumber.fhir.json")
    private Resource deductionWithEmptyNhsNumber;

    @Value("classpath:controllerTestsResources/deductionNoNhsNumber.fhir.json")
    private Resource deductionWithNoNhsNumber;

    @Value("classpath:controllerTestsResources/deductionNullNhsNumber.fhir.json")
    private Resource deductionWithNullNhsNumber;

    @Value("classpath:controllerTestsResources/deductionBlankDateOfDeduction.fhir.json")
    private Resource deductionWithBlankDateOfDeduction;

    @Value("classpath:controllerTestsResources/deductionEmptyDateOfDeduction.fhir.json")
    private Resource deductionWithEmptyDateOfDeduction;

    @Value("classpath:controllerTestsResources/deductionNoDateOfDeduction.fhir.json")
    private Resource deductionWithNoDateOfDeduction;

    @Value("classpath:controllerTestsResources/deductionNullDateOfDeduction.fhir.json")
    private Resource deductionWithNullDateOfDeduction;

    @Value("classpath:controllerTestsResources/deductionBlankReasonForDeduction.fhir.json")
    private Resource deductionWithBlankReasonForDeduction;

    @Value("classpath:controllerTestsResources/deductionEmptyReasonForDeduction.fhir.json")
    private Resource deductionWithEmptyReasonForDeduction;

    @Value("classpath:controllerTestsResources/deductionNoReasonForDeduction.fhir.json")
    private Resource deductionWithNoReasonForDeduction;

    @Value("classpath:controllerTestsResources/deductionNullReasonForDeduction.fhir.json")
    private Resource deductionWithNullReasonForDeduction;

    @Test
    void whenBlankNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithBlankNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(PatientJsonPaths.NHS_NUMBER_PATH);
    }

    @Test
    void whenEmptyNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithEmptyNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters: [element=\"value\"] Invalid attribute value \"\": Attribute value must not be empty (\"\")");
    }

    @Test
    void whenNoNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithNoNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(PatientJsonPaths.NHS_NUMBER_PATH);
    }

    @Test
    void whenNullNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithNullNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(PatientJsonPaths.NHS_NUMBER_PATH);
    }

    @Test
    void whenBlankDateOfDeduction_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithBlankDateOfDeduction.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(ParameterNames.DATE_OF_DEDUCTION);
    }

    @Test
    void whenEmptyDateOfDeduction_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithEmptyDateOfDeduction.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters: [element=\"valueString\"] Invalid attribute value \"\": Attribute value must not be empty (\"\")");
    }

    @Test
    void whenNoDateOfDeduction_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithNoDateOfDeduction.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(ParameterNames.DATE_OF_DEDUCTION);
    }

    @Test
    void whenNullDateOfDeduction_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithNullDateOfDeduction.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(ParameterNames.DATE_OF_DEDUCTION);
    }

    @Test
    void whenBlankReasonForDeduction_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithBlankReasonForDeduction.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(ParameterNames.DEDUCTION_REASON_CODE);
    }

    @Test
    void whenEmptyReasonForDeduction_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithEmptyReasonForDeduction.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters: [element=\"valueString\"] Invalid attribute value \"\": Attribute value must not be empty (\"\")");
    }

    @Test
    void whenNoReasonForDeduction_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithNoReasonForDeduction.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(ParameterNames.DEDUCTION_REASON_CODE);
    }

    @Test
    void whenNullReasonForDeduction_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithNullReasonForDeduction.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains(ParameterNames.DEDUCTION_REASON_CODE);
    }

}