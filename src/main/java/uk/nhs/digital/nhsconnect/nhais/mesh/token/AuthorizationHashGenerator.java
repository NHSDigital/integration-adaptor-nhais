package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.SneakyThrows;
import uk.nhs.digital.nhsconnect.nhais.mesh.MeshConfig;

import org.apache.commons.codec.binary.Hex;

class AuthorizationHashGenerator {

    @SneakyThrows
    public String computeHash(MeshConfig meshConfig, Nonce nonce, String timestamp) {
        String hashInput = String.join(":", meshConfig.getMailboxId(), nonce.value, nonce.count, meshConfig.getMailboxPassword(), timestamp);

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(meshConfig.getSharedKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(hashInput.getBytes(StandardCharsets.UTF_8)));
    }
}
