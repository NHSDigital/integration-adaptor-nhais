package uk.nhs.digital.nhsconnect.nhais.mesh;

public class FakeMeshConfig extends MeshConfig {
    public FakeMeshConfig() {
        super("mailboxId",
            "password",
            "SharedKey",
            "https://localhost:8829/messageexchange/",
            "", "");
    }
}
