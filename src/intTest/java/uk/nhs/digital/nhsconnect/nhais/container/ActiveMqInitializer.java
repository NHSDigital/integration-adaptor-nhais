package uk.nhs.digital.nhsconnect.nhais.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class ActiveMqInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        LOGGER.info("Overriding Spring Properties for mongodb and activemq !!!!!!!!!");

        var activeMqContainer = new ActiveMqContainer();
        activeMqContainer.start();

        var values = TestPropertyValues.of(
            "nhais.amqp.brokers=amqp://" + activeMqContainer.getContainerIpAddress() + ":" + activeMqContainer.getFirstMappedPort());

        values.applyTo(configurableApplicationContext);
    }
}