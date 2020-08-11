package uk.nhs.digital.nhsconnect.nhais.uat.common;

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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class AbstractArgumentsProvider implements ArgumentsProvider {
    protected static final String FHIR_FILE_ENDING = ".fhir.json";
    protected static final String EDIFACT_FILE_ENDING = ".edifact.dat";
    protected static final String RECEP_FILE_ENDING = ".recep.dat";

    public abstract String getFolder();

    abstract public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception;

    protected String readResource(List<Resource> resources, String fileEnding) {
        return resources.stream()
            .filter(resource -> resource.getFilename() != null)
            .filter(resource -> resource.getFilename().endsWith(fileEnding))
            .map(this::readFile)
            .findFirst()
            .orElseThrow();
    }

    @SneakyThrows
    private String readFile(Resource resource) {
        return new String(Files.readAllBytes(resource.getFile().toPath()));
    }

    protected Resource[] getResources() throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        return resolver.getResources("classpath*:/" + getFolder() + "/*/*");
    }

    protected Map<String, List<Resource>> groupedResources(Resource[] resources) {
        return Arrays.stream(resources)
            .filter(r -> !r.getFilename().endsWith("txt")) // ignore notes
            .filter(r -> !r.getFilename().contains("ignore")) // ignore ignored
            .collect(Collectors.groupingBy(resource -> {
                var pathParts = ((FileSystemResource) resource).getPath().split("/");
                var category = pathParts[pathParts.length - 2];
                var fileName = pathParts[pathParts.length - 1];
                var fileNumber = fileName.split("\\.")[0];
                return category + "/" + fileNumber;
            }));
    }
}
