package uk.nhs.digital.nhsconnect.nhais.container;

import org.testcontainers.containers.GenericContainer;

public class MongoDbContainer extends GenericContainer<MongoDbContainer> {

    public static final int MONGODB_PORT = 27017;
    public static final String DEFAULT_IMAGE_AND_TAG = "mongo:3.2.4";
    private static MongoDbContainer container;

    private MongoDbContainer() {
        super(DEFAULT_IMAGE_AND_TAG);
        addExposedPort(MONGODB_PORT);
    }

    public static MongoDbContainer getInstance() {
        if (container == null) {
            container = new MongoDbContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        var newMongoUri = "mongodb://" + getContainerIpAddress() + ":" + getMappedPort(MONGODB_PORT);
        System.setProperty("NHAIS_MONGO_URI", newMongoUri);
        System.setProperty("NHAIS_MONGO_AUTO_INDEX_CREATION", String.valueOf(true));
    }
}