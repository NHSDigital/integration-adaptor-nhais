package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MeshConfigTest {

    @Test
    public void when_certHasExtraWhitespace_then_itIsTrimmed() {
        String withWhitespace = "    asdf    \n" +
            "\tqwerty\n\n";
        String trimmed = "asdf\nqwerty";
        MeshConfig meshConfig = new MeshConfig(null, null, null, null, withWhitespace, withWhitespace);
        assertThat(meshConfig.getEndpointCert()).isEqualTo(trimmed);
        assertThat(meshConfig.getEndpointPrivateKey()).isEqualTo(trimmed);
    }


}
