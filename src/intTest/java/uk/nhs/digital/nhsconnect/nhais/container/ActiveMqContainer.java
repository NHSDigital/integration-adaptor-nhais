package uk.nhs.digital.nhsconnect.nhais.container;

import org.testcontainers.containers.GenericContainer;

public class ActiveMqContainer extends GenericContainer<ActiveMqContainer> {

    public static final int ACTIVEMQ_PORT = 5672;
    public static final String DEFAULT_IMAGE_AND_TAG = "rmohr/activemq:latest";

    public ActiveMqContainer() {
        this(DEFAULT_IMAGE_AND_TAG);
    }

    public ActiveMqContainer(String image) {
        super(image);
        addExposedPort(ACTIVEMQ_PORT);
    }

    public Integer getPort() {
        return getMappedPort(ACTIVEMQ_PORT);
    }
}