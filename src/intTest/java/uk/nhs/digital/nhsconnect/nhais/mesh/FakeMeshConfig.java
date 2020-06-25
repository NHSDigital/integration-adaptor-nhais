package uk.nhs.digital.nhsconnect.nhais.mesh;

public class FakeMeshConfig extends MeshConfig {
    public FakeMeshConfig() {
        super("mailboxId",
            "password",
            "SharedKey",
            System.getProperty("NHAIS_MESH_HOST"),
            "", "");
    }
}
