package uk.nhs.digital.nhsconnect.nhais.outbound.controller;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.outbound.OutboundQueueService;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirController;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirParser;
import uk.nhs.digital.nhsconnect.nhais.outbound.fhir.FhirToEdifactService;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("component")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FhirController.class)
public class ConversationIdHeadersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FhirParser fhirParser;

    @MockitoBean
    private OutboundQueueService outboundQueueService;

    @MockitoBean
    private FhirToEdifactService fhirToEdifactService;

    @MockitoBean
    private ConversationIdService conversationIdService;

    @Test
    void When_ConversationIdInRequestHeader_Expect_ProvidedIdIsUsed() throws Exception {
        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("text/plain")
            .header("ConversationId", "asdf1234")
            .content("qwe"))
            .andExpect(status().is(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE))
            .andExpect(header().string("ConversationId", "asdf1234"));
    }

    @Test
    void When_ConversationNotIdInRequestHeader_Expect_GeneratedIdIsUsed() throws Exception {
        when(conversationIdService.applyRandomConversationId()).thenCallRealMethod();
        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance")
            .contentType("text/plain")
            .content("qwe"))
            .andExpect(status().is(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE))
            .andExpect(header().string("ConversationId", Matchers.matchesRegex("[0-9A-F]{32}")));
    }

}
