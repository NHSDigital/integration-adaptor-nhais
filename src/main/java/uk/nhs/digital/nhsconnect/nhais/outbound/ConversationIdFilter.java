package uk.nhs.digital.nhsconnect.nhais.outbound;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class ConversationIdFilter extends OncePerRequestFilter {

    static final String HEADER_NAME = "ConversationId";

    private final ConversationIdService conversationIdService;

    public ConversationIdFilter(ConversationIdService conversationIdService) {
        this.conversationIdService = conversationIdService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
        throws java.io.IOException, ServletException {
        try {
            var token = request.getHeader(HEADER_NAME);
            if (StringUtils.isEmpty(token)) {
                token = conversationIdService.applyRandomConversationId();
            } else {
                conversationIdService.applyConversationId(token);
            }
            response.addHeader(HEADER_NAME, token);
            chain.doFilter(request, response);
        } finally {
            conversationIdService.resetConversationId();
        }
    }
}
