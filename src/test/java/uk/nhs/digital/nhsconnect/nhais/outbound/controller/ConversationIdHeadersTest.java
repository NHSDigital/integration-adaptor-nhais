package uk.nhs.digital.nhsconnect.nhais.outbound.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirController;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("component")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FhirController.class)
public class ConversationIdHeadersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FhirParser fhirParser;

    @MockBean
    private OutboundQueueService outboundQueueService;

    @MockBean
    private FhirToEdifactService fhirToEdifactService;

    @SpyBean
    private ConversationIdService conversationIdService;

    @Test
    void whenConversationIdInRequestHeader_thenProvidedIdIsUsed() throws Exception {
        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("text/plain")
            .header("ConversationId", "asdf1234")
            .content("qwe"))
            .andExpect(status().is(415))
            .andExpect(header().string("ConversationId", "asdf1234"));
    }

    @Test
    void whenConversationNotIdInRequestHeader_thenGeneratedIdIsUsed() throws Exception {
        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("text/plain")
            .content("qwe"))
            .andExpect(status().is(415))
            .andExpect(header().string("ConversationId", Matchers.matchesRegex("[0-9A-F]{32}")));
    }

}
