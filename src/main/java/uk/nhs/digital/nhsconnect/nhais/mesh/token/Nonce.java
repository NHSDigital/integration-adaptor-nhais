package uk.nhs.digital.nhsconnect.nhais.mesh.token;

import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
/**
 * Used in MESH authorization token - can by used only once per API request
 */
class Nonce {
    @NonNull
    private final String value;
    private final String count = "1"; //token should use Nonce only once

    Nonce() {
        this.value = UUID.randomUUID().toString();
    }
}
