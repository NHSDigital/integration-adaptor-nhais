package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.extern.slf4j.Slf4j;
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
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;

import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@DirtiesContext
public class AmendmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:patient/not-json.xml")
    private Resource notJsonPayload;

    @Value("classpath:patient/amendment-duplicated-patches.json")
    private Resource duplicatedPatches;

    @Value("classpath:patient/amendment-empty-patches.json")
    private Resource emptyPatches;

    @Value("classpath:patient/amendment-missing-gp-code.json")
    private Resource missingGpCode;

    @Value("classpath:patient/amendment-missing-healthcare-party-code.json")
    private Resource missingHealthcarePartyCode;

    @Value("classpath:patient/amendment-missing-nhs-number.json")
    private Resource missingNhsNumber;

    @Value("classpath:patient/amendment-missing-patches.json")
    private Resource missingPatches;

    @Value("classpath:patient/amendment-missing-gp-trading-partner-code.json")
    private Resource missingGpTradingPartnerCode;

    @Value("classpath:patient/amendment-invalid-json-structure.json")
    private Resource invalidJsonStructure;

    @Value("classpath:patient/amendment-duplicated-extension-patches.json")
    private Resource duplicateExtensionPatches;

    @Test
    void whenNotJson_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(notJsonPayload.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenDuplicatedPatches_thenRerturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(duplicatedPatches.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenMissingHealthcarePartyCode_thenRerturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(missingHealthcarePartyCode.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }


    @Test
    void whenEmptyPatches_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(emptyPatches.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenMissingNhsNumber_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(missingNhsNumber.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenMissingGpCode_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(missingGpCode.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenMissingPatches_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(missingPatches.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenMissingGpTradingPartnerCode_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(missingGpTradingPartnerCode.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenInvalidJsonStructure_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(invalidJsonStructure.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenDuplicateExtensionPatches_thenReturns400() throws Exception {
        String requestBody = new String(Files.readAllBytes(duplicateExtensionPatches.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/9999999999").contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest());
    }


}