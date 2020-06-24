package uk.nhs.digital.nhsconnect.nhais.parse;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EdifactParserV2Test {

    @Test
    void parse() throws IOException {
        try(InputStream is = this.getClass().getResourceAsStream("/edifact/multi_transaction.edifact.dat")) {
            String payload = IOUtils.toString(is, StandardCharsets.UTF_8);

            new EdifactParserV2().parse(payload);
        }
    }
}
