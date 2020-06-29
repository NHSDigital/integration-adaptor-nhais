package uk.nhs.digital.nhsconnect.nhais.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;

class MessageList extends EdifactList<Message> {

    private final List<Message> messages;

    public MessageList(List<String> allEdifactSegments) {
        var allMessageHeaderSegmentIndexes = findAllIndexesOfSegment(allEdifactSegments, MessageHeader.KEY);
        var allMessageTrailerSegmentIndexes = findAllIndexesOfSegment(allEdifactSegments, MessageTrailer.KEY);

        var messageHeaderTrailerIndexPairs = zipIndexes(allMessageHeaderSegmentIndexes, allMessageTrailerSegmentIndexes);

        this.messages = messageHeaderTrailerIndexPairs.stream()
            .map(messageStartEndIndexPair ->
                allEdifactSegments.subList(messageStartEndIndexPair.getLeft(), messageStartEndIndexPair.getRight() + 1))
            .map(this::parseMessage)
            .collect(Collectors.toList());
    }

    private Message parseMessage(List<String> singleMessageEdifactSegments) {
        var firstTransactionStartIndex = findAllIndexesOfSegment(singleMessageEdifactSegments, SegmentGroup.KEY_01).stream()
            .findFirst()
            // there might be no transaction inside - RECEP - so all message lines belong to message
            .orElse(singleMessageEdifactSegments.size() - 1);

        var onlyMessageLines = new ArrayList<>(singleMessageEdifactSegments.subList(0, firstTransactionStartIndex)); // first lines until transaction
        onlyMessageLines.add(singleMessageEdifactSegments.get(singleMessageEdifactSegments.size() - 1)); // message trailer

        var message = new Message(onlyMessageLines);
        TransactionList transactions = new TransactionList(singleMessageEdifactSegments);
        transactions.forEach(transaction -> transaction.setMessage(message));
        message.setTransactions(transactions);

        return message;
    }

    @Override
    public Message get(int index) {
        return messages.get(index);
    }

    @Override
    public int size() {
        return messages.size();
    }
}
