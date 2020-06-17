package uk.nhs.digital.nhsconnect.nhais.uat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CustomArgumentsProvider implements ArgumentsProvider {

    private final String folder;

    public CustomArgumentsProvider(String folder) {
        this.folder = folder;
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
        var resources = getResources();

        var grouped = Arrays.stream(resources)
            .collect(Collectors.groupingBy(resource -> {
                var pathParts = ((FileSystemResource) resource).getPath().split("/");
                var category = pathParts[pathParts.length - 2];
                var fileName = pathParts[pathParts.length - 1];
                var fileNumber = fileName.split("\\.")[0];
                return category + "/" + fileNumber;
            })).entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                es -> {
                    if (es.getValue().size() != 2) {
                        throw new IllegalStateException("There should be 2 test data files: N.edifact.dat and N.fhir.json");
                    }
                    String edifact = readFile(es.getValue().get(0));
                    String fhir = readFile(es.getValue().get(1));

                    return TestData.builder()
                        .edifact(edifact)
                        .fhir(fhir)
                        .build();
                }));

        return grouped.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(es -> Arguments.of(es.getKey(), es.getValue()));
    }

    @SneakyThrows
    private String readFile(Resource resource) {
        return new String(Files.readAllBytes(resource.getFile().toPath()));
    }

    private Resource[] getResources() throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        return resolver.getResources("classpath*:/" + folder + "/*/*");
    }

    public static class Inbound extends CustomArgumentsProvider {
        public Inbound() {
            super("inbound_uat_data");
        }
    }

    public static class Outbound extends CustomArgumentsProvider {
        public Outbound() {
            super("outbound_uat_data");
        }
    }
}