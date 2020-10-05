package uk.nhs.digital.nhsconnect.nhais.utils;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConversationIdService {

    private static final String MDC_KEY = "ConversationId";

    public void applyConversationId(String id) {
        MDC.put(MDC_KEY, id);
    }

    public String applyRandomConversationId() {
        var id = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        applyConversationId(id);
        return id;
    }

    public void resetConversationId() {
        MDC.remove(MDC_KEY);
    }

    public String getCurrentConversationId() {
        return MDC.get(MDC_KEY);
    }

}
