package uk.nhs.digital.nhsconnect.nhais.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;

@Slf4j
public class FakeMeshContainer extends GenericContainer<FakeMeshContainer> {

    public static final int FAKE_MESH_PORT = 8829;
    private static FakeMeshContainer container;

    private FakeMeshContainer() {
        super(new ImageFromDockerfile()
            .withFileFromPath("Dockerfile", Path.of("./fake-mesh/Dockerfile"))
        );
        addExposedPort(FAKE_MESH_PORT);
    }

    public static FakeMeshContainer getInstance() {
        if (container == null) {
            container = new FakeMeshContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        var fakeMeshUri = "https://" + getContainerIpAddress() + ":" + getMappedPort(FAKE_MESH_PORT) + "/messageexchange/";
        LOGGER.info("Changing fake MESH URI (NHAIS_MESH_HOST) to {}", fakeMeshUri);
        System.setProperty("NHAIS_MESH_HOST", fakeMeshUri);
    }
}