package uk.nhs.digital.nhsconnect.nhais.utils;

import com.google.common.hash.Hashing;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;

public class OperationIdUtils {
    public static String buildOperationId(@NonNull String organization, @NonNull Long transactionNumber) {
        return Hashing.sha256()
            .hashString(organization + transactionNumber, StandardCharsets.UTF_8)
            .toString();
    }
}
