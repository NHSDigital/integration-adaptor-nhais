package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.OperationOutcome;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import java.io.IOException;
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

    @Value("classpath:controllerTestsResources/deductionNoNhsNumber.fhir.json")
    private Resource deductionWithNullNhsNumber;

    @Test
    void whenBlankNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithBlankNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
    }

    @Test
    void whenEmptyNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithEmptyNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters: Invalid attribute value \"\": Attribute values must not be empty (\"\")");
    }


    @Test
    void whenNoNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithNoNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
    }

    @Test
    void whenNullNhsNumber_thenRespond400() throws Exception {
        String requestBody = new String(Files.readAllBytes(deductionWithNullNhsNumber.getFile().toPath()));
        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest())
            .andReturn();
        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
    }

//    @Test
//    void whenBlankDateOfDeduction_thenRespond400() throws Exception {
//        String requestBody = new String(Files.readAllBytes(deductionWithBlankDateOfDeduction.getFile().toPath()));
//        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
//        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
//    }
//
//
//    @Test
//    void whenEmptyDateOfDeduction_thenRespond400() throws Exception {
//        String requestBody = new String(Files.readAllBytes(deductionWithEmptyDateOfDeduction.getFile().toPath()));
//        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
//        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters: Invalid attribute value \"\": Attribute values must not be empty (\"\")");
//    }
//
//
//    @Test
//    void whenNoDateOfDeduction_thenRespond400() throws Exception {
//        String requestBody = new String(Files.readAllBytes(deductionWithNoDateOfDeduction.getFile().toPath()));
//        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
//        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
//    }
//
//    @Test
//    void whenNullDateOfDeduction_thenRespond400() throws Exception {
//        String requestBody = new String(Files.readAllBytes(deductionWithNullDateOfDeduction.getFile().toPath()));
//        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
//        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
//    }
//
//    @Test
//    void whenBlankReasonForDeduction_thenRespond400() throws Exception {
//        String requestBody = new String(Files.readAllBytes(deductionWithBlankReasonForDeduction.getFile().toPath()));
//        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
//        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
//    }
//
//
//    @Test
//    void whenEmptyReasonForDeduction_thenRespond400() throws Exception {
//        String requestBody = new String(Files.readAllBytes(deductionWithEmptyReasonForDeduction.getFile().toPath()));
//        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
//        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("Unable to parse JSON resource as a Parameters: Invalid attribute value \"\": Attribute values must not be empty (\"\")");
//    }
//
//
//    @Test
//    void whenNoReasonForDeduction_thenRespond400() throws Exception {
//        String requestBody = new String(Files.readAllBytes(deductionWithNoReasonForDeduction.getFile().toPath()));
//        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
//        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
//    }
//
//    @Test
//    void whenNullReasonForDeduction_thenRespond400() throws Exception {
//        String requestBody = new String(Files.readAllBytes(deductionWithNullReasonForDeduction.getFile().toPath()));
//        MvcResult result = mockMvc.perform(post(URL).contentType("application/json").content(requestBody))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//        OperationOutcome operationOutcome = (OperationOutcome) fhirParser.parse(result.getResponse().getContentAsString());
//        assertThat(operationOutcome.getIssueFirstRep().getDetails().getText()).contains("/identifier/0/value");
//    }

}