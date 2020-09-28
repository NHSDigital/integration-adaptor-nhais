package uk.nhs.digital.nhsconnect.nhais.outbound.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirController;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirToEdifactService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("component")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FhirController.class)
public class CorrelationIdControllerTest {

@Autowired
    private MockMvc mockMvc;

    @MockBean
    private FhirParser fhirParser;

    @MockBean
    private OutboundQueueService outboundQueueService;

    @MockBean
    private FhirToEdifactService fhirToEdifactService;

    @Test
    void whenCorrelationIdInRequestHeader_thenProvidedIdIsUsed() throws Exception {
        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("text/plain")
            .header("CorrelationId", "asdf1234")
            .content("qwe"))
            .andExpect(status().is(415))
            .andExpect(header().string("CorrelationId", "asdf1234"));
    }

    @Test
    void whenCorrelationNotIdInRequestHeader_thenGeneratedIdIsUsed() throws Exception {
        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("text/plain")
            .content("qwe"))
            .andExpect(status().is(415))
            .andExpect(header().string("CorrelationId", Matchers.matchesRegex("[0-9A-F]{32}")));
    }

}
