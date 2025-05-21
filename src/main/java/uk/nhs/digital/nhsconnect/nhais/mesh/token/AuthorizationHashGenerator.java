package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.SneakyThrows;
import uk.nhs.digital.nhsconnect.nhais.mesh.http.MeshConfig;

import org.apache.commons.codec.binary.Hex;

class AuthorizationHashGenerator {

    private static final String HMAC_SHA256_ALGORITHM_NAME = "HmacSHA256";

    @SneakyThrows
    public String computeHash(MeshConfig meshConfig, Nonce nonce, String timestamp) {
        String hashInput = String.join(
            ":",
            meshConfig.getMailboxId(),
            nonce.value,
            nonce.count,
            meshConfig.getMailboxPassword(),
            timestamp
        );

        Mac sha256HMAC = Mac.getInstance(HMAC_SHA256_ALGORITHM_NAME);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            meshConfig.getSharedKey().getBytes(StandardCharsets.UTF_8),
            HMAC_SHA256_ALGORITHM_NAME
        );
        sha256HMAC.init(secretKeySpec);

        return Hex.encodeHexString(sha256HMAC.doFinal(hashInput.getBytes(StandardCharsets.UTF_8)));
    }
}
