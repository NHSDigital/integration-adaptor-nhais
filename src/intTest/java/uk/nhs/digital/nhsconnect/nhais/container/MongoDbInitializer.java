package uk.nhs.digital.nhsconnect.nhais.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        var mongoDbContainer = new MongoDbContainer();
        mongoDbContainer.start();

        var newValues = new String[] {
            "spring.data.mongodb.uri=mongodb://" + mongoDbContainer.getContainerIpAddress() + ":" + mongoDbContainer.getFirstMappedPort(),
            "spring.data.mongodb.autoIndexCreation=true"
        };

        LOGGER.info("Overriding Spring Properties for mongodb with: {}", String.join(", ", newValues));

        TestPropertyValues.of(newValues).applyTo(configurableApplicationContext);
    }
}