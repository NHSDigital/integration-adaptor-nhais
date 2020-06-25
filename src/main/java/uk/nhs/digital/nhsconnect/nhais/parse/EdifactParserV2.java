package uk.nhs.digital.nhsconnect.nhais.parse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.InterchangeV2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Comparators;
import com.google.common.collect.Streams;

public class EdifactParserV2 {
    public InterchangeV2 parse(String edifact) {
        List<String> edifactSegments = parseIntoEdifactSegments(edifact);

        var interchange = new InterchangeV2(new InterchangeSegments(edifactSegments), new MessageSegments(edifactSegments));

        validateInterchange(interchange);

        return interchange;
    }

    private List<String> parseIntoEdifactSegments(String edifact) {
        return Arrays.asList(Split.bySegmentTerminator(edifact.replaceAll("\\n", "").strip()));
    }

    private void validateInterchange(InterchangeV2 interchange) {
        List<ToEdifactParsingException> exceptions = interchange.validate();
        String errorList = exceptions.stream()
            .map(Exception::getMessage)
            .collect(Collectors.joining(", "));

        if (StringUtils.isNotEmpty(errorList)) {
            throw new ToEdifactParsingException(errorList);
        }
    }

    public static List<Pair<Integer, Integer>> zipIndexes(List<Integer> startIndexes, List<Integer> endIndexes) {
        if (startIndexes.size() != endIndexes.size()) {
            throw new ToEdifactParsingException(
                "Message header-trailer count mismatch: " + startIndexes.size() + "-" + endIndexes.size());
        }

        var indexPairs = Streams.zip(startIndexes.stream(), endIndexes.stream(), Pair::of)
            .collect(Collectors.toList());

        if (!areIndexesInOrder(indexPairs)) {
            throw new ToEdifactParsingException("Message trailer before message header");
        }

        return indexPairs;
    }

    public static List<Integer> findAllIndexes(List<String> list, String key) {
        return list.stream()
            .filter(item -> item.startsWith(key))
            .map(list::indexOf)
            .collect(Collectors.toList());
    }

    public static boolean areIndexesInOrder(List<Pair<Integer, Integer>> messageIndexPairs) {
        return Comparators.isInOrder(
            messageIndexPairs.stream()
                .map(pair -> List.of(pair.getLeft(), pair.getRight()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()),
            Comparator.naturalOrder());
    }
}
