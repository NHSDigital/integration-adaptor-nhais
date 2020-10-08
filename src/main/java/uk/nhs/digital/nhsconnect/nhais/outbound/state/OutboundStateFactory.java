package uk.nhs.digital.nhsconnect.nhais.outbound.state;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.mesh.message.WorkflowId;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Message;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OutboundStateFactory {

    private final ConversationIdService conversationIdService;

    public OutboundState fromRecep(Message message) {
        var interchangeHeader = message.getInterchange().getInterchangeHeader();
        var messageHeader = message.getMessageHeader();
        var translationTimestamp = interchangeHeader.getTranslationTime();

        return new OutboundState()
            .setWorkflowId(WorkflowId.RECEP)
            .setInterchangeSequence(interchangeHeader.getSequenceNumber())
            .setMessageSequence(messageHeader.getSequenceNumber())
            .setSender(interchangeHeader.getSender())
            .setRecipient(interchangeHeader.getRecipient())
            .setTranslationTimestamp(translationTimestamp)
            .setConversationId(conversationIdService.getCurrentConversationId());
    }

}
