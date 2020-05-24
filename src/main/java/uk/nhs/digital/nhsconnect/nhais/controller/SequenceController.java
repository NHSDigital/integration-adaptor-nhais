package uk.nhs.digital.nhsconnect.nhais.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.nhsconnect.nhais.service.SequenceService;

@RestController
public class SequenceController {
    @Autowired
    private SequenceService sequenceService;

    @GetMapping(path = "/seq/")
    @ResponseStatus(HttpStatus.OK)
    public String getSeq() {
        System.out.println(sequenceService.generateTransactionId());
        System.out.println(sequenceService.generateInterchangeId("test-sender1", "test-recipient1"));
        System.out.println(sequenceService.generateMessageId("test-sender2", "test-recipient2"));

        return ("OK");
    }
}

