package uk.nhs.digital.nhsconnect.nhais.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.parse.FhirParser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("component")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AmendmentController.class)
public class AmendmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FhirParser fhirParser;

    @Test
    void whenValidInput_thenReturns202() throws Exception {
        mockMvc.perform(patch("/fhir/Patient/12345")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isAccepted());
    }
}
