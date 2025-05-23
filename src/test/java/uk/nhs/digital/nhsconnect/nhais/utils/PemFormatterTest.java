package uk.nhs.digital.nhsconnect.nhais.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PemFormatterTest {

    @Test
    public void When_CertHasExtraWhitespace_Expect_ItIsTrimmed() {

        // this warning is suppressed as the extra whitespace (including trailing whitespace) is intentional
        @SuppressWarnings("com.puppycrawl.tools.checkstyle.checks.regexp.RegexpSingleLineCheck")
        final String withWhitespace = """
             -----BEGIN CERTIFICATE-----
                MIIFXzCCA0egAwIBAgIJALRbCSor9bEbMA0GCSqGSIb3DQEBCwUAMEUxCzAJBgNV 
                
                
                W/JNIRmhLoeFNGNh8HvhI2PwOCsFiqT1rrCaUtusTyH0Ggs=
            
               -----END CERTIFICATE-----    
            """;

        final String trimmed = """
        -----BEGIN CERTIFICATE-----
        MIIFXzCCA0egAwIBAgIJALRbCSor9bEbMA0GCSqGSIb3DQEBCwUAMEUxCzAJBgNV
        W/JNIRmhLoeFNGNh8HvhI2PwOCsFiqT1rrCaUtusTyH0Ggs=
        -----END CERTIFICATE-----""";

        String formatted = PemFormatter.format(withWhitespace);
        assertThat(formatted).isEqualTo(trimmed);
        assertThat(formatted).isEqualTo(trimmed);
    }

    @Test
    public void When_CertHasNoNewlines_Expect_ItIsReformatted() {
        String withoutNewlines =
            "-----BEGIN RSA PRIVATE KEY-----"
                + " MIIJKQIBAAKCAgEA0x7V2cpEuXbLxb4TFigeN6e/TViXx4B9LMuHwwENX1P5V3O5"
                + " M0d/fLCFruu5dU3PWKoU2rTzUkflj5XOzu2xAftYi3KDMzRR2sByxjjxb/qMIybG"
                + " -----END RSA PRIVATE KEY-----";

        String trimmed = """
            -----BEGIN RSA PRIVATE KEY-----
            MIIJKQIBAAKCAgEA0x7V2cpEuXbLxb4TFigeN6e/TViXx4B9LMuHwwENX1P5V3O5
            M0d/fLCFruu5dU3PWKoU2rTzUkflj5XOzu2xAftYi3KDMzRR2sByxjjxb/qMIybG
            -----END RSA PRIVATE KEY-----""";

        String formatted = PemFormatter.format(withoutNewlines);
        assertThat(formatted).isEqualTo(trimmed);
        assertThat(formatted).isEqualTo(trimmed);
    }

    @Test
    public void When_CertUsesDifferentHeaderAndFormattedCorrectly_Expect_ItIsNotModified() {
        String pem = """
            -----BEGIN PRIVATE KEY-----
            MIIJKQIBAAKCAgEA0x7V2cpEuXbLxb4TFigeN6e/TViXx4B9LMuHwwENX1P5V3O5
            M0d/fLCFruu5dU3PWKoU2rTzUkflj5XOzu2xAftYi3KDMzRR2sByxjjxb/qMIybG
            -----END PRIVATE KEY-----""";

        String formatted = PemFormatter.format(pem);
        assertThat(formatted).isEqualTo(pem);
        assertThat(formatted).isEqualTo(pem);
    }
}
