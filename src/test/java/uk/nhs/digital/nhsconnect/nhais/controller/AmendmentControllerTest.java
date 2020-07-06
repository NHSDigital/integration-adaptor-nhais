package uk.nhs.digital.nhsconnect.nhais.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("component")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AmendmentController.class)
public class AmendmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FhirParser fhirParser;

    @Value("classpath:/amendment/amendment.json")
    private Resource paramsPayload;

    @Test
    void whenValidInput_thenReturns202() throws Exception {
        String requestBody = new String(Files.readAllBytes(paramsPayload.getFile().toPath()));
        mockMvc.perform(patch("/fhir/Patient/1234567890")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isAccepted());
    }
}
