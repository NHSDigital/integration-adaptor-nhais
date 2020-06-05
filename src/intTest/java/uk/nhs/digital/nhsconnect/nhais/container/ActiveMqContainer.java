package uk.nhs.digital.nhsconnect.nhais.container;

import org.testcontainers.containers.GenericContainer;

public class ActiveMqContainer extends GenericContainer<ActiveMqContainer> {

    public static final int ACTIVEMQ_PORT = 5672;
    public static final String DEFAULT_IMAGE_AND_TAG = "rmohr/activemq:latest";
    private static ActiveMqContainer container;

    private ActiveMqContainer() {
        super(DEFAULT_IMAGE_AND_TAG);
        addExposedPort(ACTIVEMQ_PORT);
    }

    public static ActiveMqContainer getInstance() {
        if (container == null) {
            container = new ActiveMqContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        var containerBrokerUri = "amqp://" + getContainerIpAddress() + ":" + getMappedPort(ACTIVEMQ_PORT);
        System.setProperty("NHAIS_AMQP_BROKERS", containerBrokerUri);
    }
}