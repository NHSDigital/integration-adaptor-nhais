package uk.nhs.digital.nhsconnect.nhais.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        LOGGER.info("Overriding Spring Properties for mongodb and activemq !!!!!!!!!");

        var mongoDbContainer = new MongoDbContainer();
        mongoDbContainer.start();

        var values = TestPropertyValues.of("nhais.mongo.host=" + mongoDbContainer.getContainerIpAddress(),
            "nhais.mongo.port=" + mongoDbContainer.getFirstMappedPort());

        values.applyTo(configurableApplicationContext);
    }
}