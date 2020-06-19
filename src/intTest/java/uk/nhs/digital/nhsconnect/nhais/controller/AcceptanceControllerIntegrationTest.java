package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.IntegrationTestsExtension;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundState;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;

import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AcceptanceControllerIntegrationTest {

    private static final String expectedOperationId = "c35b1432682a04ca77b287b11f240fbd8c46f22fe589b513a84e307b57dcb820";

    @Autowired
    OutboundStateRepository outboundStateRepository;
    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:patient/parameters.json")
    private Resource paramsPayload;

    @Test
    void whenValidInput_thenReturns202() throws Exception {
        String requestBody = new String(Files.readAllBytes(paramsPayload.getFile().toPath()));

        mockMvc.perform(post("/fhir/Patient/$nhais.acceptance").contentType("application/json").content(requestBody))
            .andExpect(status().isAccepted())
            .andExpect(header().string("OperationId", expectedOperationId));

        Iterable<OutboundState> outboundState = outboundStateRepository.findAll();

        //TODO: assert outboundStateDao contains expected data
        //TODO: read the item off the queue and check it
    }
}