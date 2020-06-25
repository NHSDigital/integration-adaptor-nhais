package uk.nhs.digital.nhsconnect.nhais.container;

import java.nio.file.Path;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

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
    }
}