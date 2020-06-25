package uk.nhs.digital.nhsconnect.nhais.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionNumber;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.MessageV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Comparators;
import com.google.common.collect.Streams;

public class TransactionSegments {
    private final List<String> edifactSegments;

    public TransactionSegments(List<String> edifactSegments) {
        this.edifactSegments = List.copyOf(edifactSegments);
    }

    public List<TransactionV2> toTransactions() {
        var transactionStartIndexes = EdifactParserV2.findAllIndexes(edifactSegments, ReferenceTransactionNumber.KEY_QUALIFIER);
        var transactionEndIndexes = new ArrayList<>(transactionStartIndexes);
        transactionEndIndexes.remove(0); // there is no tran end so ending index is the beginning of the next one so end is beginning shifted 1 right
        transactionEndIndexes.add(edifactSegments.size() - 1); // to remove message trailer

        var transactionIndexPairs = EdifactParserV2.zipIndexes(transactionStartIndexes, transactionEndIndexes);

        return transactionIndexPairs.stream()
            .map(transactionIndexPair -> edifactSegments.subList(transactionIndexPair.getLeft(), transactionIndexPair.getRight()))
            .map(TransactionV2::new)
            .collect(Collectors.toList());
    }

    public int getFirstTransactionIndex(){
        return EdifactParserV2.findAllIndexes(edifactSegments, ReferenceTransactionNumber.KEY_QUALIFIER).get(0);
    }

}
