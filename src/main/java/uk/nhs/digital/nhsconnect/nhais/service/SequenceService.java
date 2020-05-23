package uk.nhs.digital.nhsconnect.nhais.service;

import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.nhsconnect.nhais.exceptions.SenderAndRecipientValidationException;
import uk.nhs.digital.nhsconnect.nhais.repository.SequenceRepository;

@Service
public class SequenceService {
    private final static String TRANSACTION_ID = "transaction_id";
    private final static String INTERCHANGE_FORMAT = "SIS-%s-%s";
    private final static String INTERCHANGE_MESSAGE_FORMAT = "SMS-%s-%s";

    @Autowired
    private SequenceRepository sequenceRepository;

    public String generateTransactionId() {
        return String.valueOf(getNextSequence(TRANSACTION_ID));
    }

    public String generateInterchangeId(String sender, String recipient) {
        validateSenderAndRecipient(sender, recipient);
        return String.valueOf(getNextSequence(String.format(INTERCHANGE_FORMAT, sender, recipient)));
    }

    public String generate_message_id(String sender, String recipient) {
        validateSenderAndRecipient(sender, recipient);
        return String.valueOf(getNextSequence(String.format(INTERCHANGE_MESSAGE_FORMAT, sender, recipient)));
    }

    private void validateSenderAndRecipient(String sender, String recipient) {
        if (StringUtils.isBlank(sender) || StringUtils.isBlank(recipient)) {
            throw new SenderAndRecipientValidationException(
                    String.format("Sender or recipient not valid. Sender: %s, recipient: %s", sender, recipient)
            );
        }
    }

    private Long getNextSequence(String key) {
        Long seqNumber;
        if (existsByKey(key)) {
            seqNumber = sequenceRepository.getNext(key);
        } else {
            seqNumber = addSequenceKey(key);
        }

        return seqNumber;
    }

    private boolean existsByKey(String key) {
        return sequenceRepository.existsByKey(key);
    }

    private Long addSequenceKey(String key) {
        return sequenceRepository.addSequenceKey(key);
    }
}
