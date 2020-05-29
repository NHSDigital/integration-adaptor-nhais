package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EdifactFixtures {

    public static final String STATE_ONLY_WITH_NEWLINES = resourceToString("/edifact/state-only-with-newlines.dat");
    public static final String STATE_ONLY_WITHOUT_NEWLINES = resourceToString("/edifact/state-only-without-newlines.dat");

    private static String resourceToString(String resource) {
        try {
            return IOUtils.resourceToString(resource, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
