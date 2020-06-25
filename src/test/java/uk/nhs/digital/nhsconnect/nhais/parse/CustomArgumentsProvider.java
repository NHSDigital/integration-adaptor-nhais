package uk.nhs.digital.nhsconnect.nhais.parse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
        var resources = getResources();

        var grouped = Arrays.stream(resources)
            .filter(r -> !r.getFilename().contains("ignore")) // ignore ignored
            .collect(Collectors.groupingBy(resource -> {
                var pathParts = ((FileSystemResource) resource).getPath().split("/");
                return pathParts[pathParts.length - 2];
            })).entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                es -> {
                    var messages = es.getValue().stream()
                        .filter(resource -> Pattern.matches("output\\.message\\.\\d+\\.dat", resource.getFilename()))
                        .map(messageResources -> {
                            var messageContent = Arrays.asList(readFile(messageResources).split("\\n"));
                            var messageFileNameWithoutExtension = FilenameUtils.removeExtension(messageResources.getFilename());

                            var transactions = es.getValue().stream()
                                .filter(resource -> Pattern.matches(messageFileNameWithoutExtension + "\\.transaction\\.\\d+\\.dat", resource.getFilename()))
                                .map(this::readFile)
                                .map(content -> content.split("\\n"))
                                .map(Arrays::asList)
                                .map(Transaction::new)
                                .collect(Collectors.toList());

                            return new Message(messageContent, transactions);
                        })
                        .collect(Collectors.toList());

                    var inputContent = readFile(es.getValue().stream()
                        .filter(resource -> resource.getFilename().equals("input.dat"))
                        .findFirst()
                        .orElseThrow());

                    var interchange = es.getValue().stream()
                        .filter(resource -> resource.getFilename().equals("output.interchange.dat"))
                        .findFirst()
                        .map(this::readFile)
                        .map(content -> content.split("\\n"))
                        .map(Arrays::asList)
                        .map(interchangeContent -> new Interchange(interchangeContent, messages))
                        .orElseThrow();

                    return new TestData(inputContent, interchange);
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
        return resolver.getResources("classpath*:parser/*/*");
    }

    @Getter
    @RequiredArgsConstructor
    public static class TestData {
        private final String input;
        private final Interchange interchange;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Interchange {
        private final List<String> edifactSegments;
        private final List<Message> messages;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Message {
        private final List<String> edifactSegments;
        private final List<Transaction> transactions;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Transaction {
        private final List<String> edifactSegments;
    }
}
