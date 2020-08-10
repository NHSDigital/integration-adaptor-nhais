package uk.nhs.digital.nhsconnect.nhais.uat.common;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutboundArgumentsProvider extends AbstractArgumentsProvider {

    @Override
    public String getFolder() {
        return "outbound_uat_data";
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
        var resources = getResources();

        var grouped = groupedResources(resources)
            .entrySet().stream()
            .peek(es -> {
                if (es.getValue().size() != 2) {
                    throw new IllegalStateException(String.format(
                        "There should be 2 test data files: 'N.<any>%s' and 'N.<any>%s': %s", FHIR_FILE_ENDING, EDIFACT_FILE_ENDING, es.getKey()));
                }
            })
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                es -> TestData.builder()
                    .edifact(readResource(es.getValue(),EDIFACT_FILE_ENDING))
                    .json(readResource(es.getValue(),FHIR_FILE_ENDING))
                    .build()));

        return grouped.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(es -> Arguments.of(es.getKey(), es.getValue()));
    }

}