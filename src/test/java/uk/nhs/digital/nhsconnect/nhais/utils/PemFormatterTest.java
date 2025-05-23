package uk.nhs.digital.nhsconnect.nhais.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PemFormatterTest {

    @Test
    public void When_CertHasExtraWhitespace_Expect_ItIsTrimmed() {
        String withWhitespace = " -----BEGIN CERTIFICATE-----\n " +
            "    \t  MIIFXzCCA0egAwIBAgIJALRbCSor9bEbMA0GCSqGSIb3DQEBCwUAMEUxCzAJBgNV \n" +
            "  \n\n    W/JNIRmhLoeFNGNh8HvhI2PwOCsFiqT1rrCaUtusTyH0Ggs=\n" +
            "   \r   -----END CERTIFICATE-----";
        String trimmed = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFXzCCA0egAwIBAgIJALRbCSor9bEbMA0GCSqGSIb3DQEBCwUAMEUxCzAJBgNV\n" +
            "W/JNIRmhLoeFNGNh8HvhI2PwOCsFiqT1rrCaUtusTyH0Ggs=\n" +
            "-----END CERTIFICATE-----";
        String formatted = PemFormatter.format(withWhitespace);
        assertThat(formatted).isEqualTo(trimmed);
        assertThat(formatted).isEqualTo(trimmed);
    }

    @Test
    public void When_CertHasNoNewlines_Expect_ItIsReformatted() {
        String withoutNewlines = "-----BEGIN RSA PRIVATE KEY-----" +
            " MIIJKQIBAAKCAgEA0x7V2cpEuXbLxb4TFigeN6e/TViXx4B9LMuHwwENX1P5V3O5" +
            " M0d/fLCFruu5dU3PWKoU2rTzUkflj5XOzu2xAftYi3KDMzRR2sByxjjxb/qMIybG" +
            " -----END RSA PRIVATE KEY-----";
        String trimmed = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIJKQIBAAKCAgEA0x7V2cpEuXbLxb4TFigeN6e/TViXx4B9LMuHwwENX1P5V3O5\n" +
            "M0d/fLCFruu5dU3PWKoU2rTzUkflj5XOzu2xAftYi3KDMzRR2sByxjjxb/qMIybG\n" +
            "-----END RSA PRIVATE KEY-----";
        String formatted = PemFormatter.format(withoutNewlines);
        assertThat(formatted).isEqualTo(trimmed);
        assertThat(formatted).isEqualTo(trimmed);
    }

    @Test
    public void When_CertUsesDifferentHeaderAndFormattedCorrectly_Expect_ItIsNotModified() {
        String pem = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIJKQIBAAKCAgEA0x7V2cpEuXbLxb4TFigeN6e/TViXx4B9LMuHwwENX1P5V3O5\n" +
            "M0d/fLCFruu5dU3PWKoU2rTzUkflj5XOzu2xAftYi3KDMzRR2sByxjjxb/qMIybG\n" +
            "-----END PRIVATE KEY-----";
        String formatted = PemFormatter.format(pem);
        assertThat(formatted).isEqualTo(pem);
        assertThat(formatted).isEqualTo(pem);
    }


}
