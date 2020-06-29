package uk.nhs.digital.nhsconnect.nhais.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Interchange;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class EdifactParser {
    public Interchange parse(String edifact) {
        var allEdifactSegments = Arrays.asList(Split.bySegmentTerminator(edifact.replaceAll("\\n", "").strip()));

        var interchange = parseInterchange(allEdifactSegments);

        validateInterchange(interchange);

        return interchange;
    }

    private Interchange parseInterchange(List<String> allEdifactSegments) {
        Interchange interchange = new Interchange(extractInterchangeEdifactSegments(allEdifactSegments));

        MessageList messages = new MessageList(allEdifactSegments);
        messages.forEach(message -> message.setInterchange(interchange));
        interchange.setMessages(messages);

        return interchange;
    }

    private List<String> extractInterchangeEdifactSegments(List<String> allEdifactSegments) {
        var firstMessageHeaderIndex = findAllIndexesOfSegment(allEdifactSegments, MessageHeader.KEY).get(0);
        var allMessageTrailerIndexes = findAllIndexesOfSegment(allEdifactSegments, MessageTrailer.KEY);
        var lastMessageTrailerIndex = allMessageTrailerIndexes.get(allMessageTrailerIndexes.size() - 1);

        var segmentsBeforeFirstMessageHeader = allEdifactSegments.subList(0, firstMessageHeaderIndex);
        var segmentsAfterLastMessageTrailer = allEdifactSegments.subList(lastMessageTrailerIndex + 1, allEdifactSegments.size());

        return Stream.of(segmentsBeforeFirstMessageHeader, segmentsAfterLastMessageTrailer)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<Integer> findAllIndexesOfSegment(List<String> list, String key) {
        var indexes = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(key)) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private void validateInterchange(Interchange interchange) {
        List<ToEdifactParsingException> exceptions = interchange.validate();
        String errorList = exceptions.stream()
            .map(Exception::getMessage)
            .collect(Collectors.joining(", "));

        if (StringUtils.isNotEmpty(errorList)) {
            throw new ToEdifactParsingException(errorList);
        }
    }
}
