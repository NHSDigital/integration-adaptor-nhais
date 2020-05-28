package uk.nhs.digital.nhsconnect.nhais.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateDAO;
import uk.nhs.digital.nhsconnect.nhais.repository.OutboundStateRepository;
import uk.nhs.digital.nhsconnect.nhais.service.OutboundMeshService;

import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = AcceptanceControllerIntegrationTest.MongoDbInitializer.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AcceptanceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:patient.json")
    private Resource patientPayload;

    @Autowired
    OutboundStateRepository outboundStateRepository;

    @Autowired
    OutboundMeshService outboundMeshService;

    private static MongoDbContainer mongoDbContainer;
    private static ActiveMqContainer activeMqContainer;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        mongoDbContainer = new MongoDbContainer();
        mongoDbContainer.start();
        activeMqContainer = new ActiveMqContainer();
        activeMqContainer.start();
    }

    @Test
    void whenValidInput_thenReturns202() throws Exception {
        String requestBody = new String(Files.readAllBytes(patientPayload.getFile().toPath()));

        mockMvc.perform(post("/fhir/Patient/12345").contentType("application/json").content(requestBody))
                .andExpect(status().isAccepted());

        Iterable<OutboundStateDAO> outboundStateDAO = outboundStateRepository.findAll();

        //TODO: assert outboundStateDao contains expected data
        //TODO: read the item off the queue and check it
        var message = outboundMeshService.receive();
        int i = 1;
    }

    public static class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            LOGGER.info("Overriding Spring Properties for mongodb and activemq !!!!!!!!!");

            TestPropertyValues values = TestPropertyValues.of("nhais.mongo.host=" + mongoDbContainer.getContainerIpAddress(),
                    "nhais.mongo.port=" + mongoDbContainer.getFirstMappedPort(),
                    "nhais.amqp.brokers=amqp://" + activeMqContainer.getContainerIpAddress() + ":" + activeMqContainer.getFirstMappedPort());

            values.applyTo(configurableApplicationContext);
        }
    }
}
