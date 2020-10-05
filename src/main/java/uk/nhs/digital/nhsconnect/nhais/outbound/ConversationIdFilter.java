package uk.nhs.digital.nhsconnect.nhais.outbound;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.nhs.digital.nhsconnect.nhais.utils.ConversationIdService;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class ConversationIdFilter extends OncePerRequestFilter {

    static final String HEADER_NAME = "ConversationId";

    private ConversationIdService conversationIdService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
        throws java.io.IOException, ServletException {
        lazyInitialize(request);
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

    private void lazyInitialize(HttpServletRequest request) {
        if (conversationIdService == null) {
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            this.conversationIdService = webApplicationContext.getBean(ConversationIdService.class);
        }
    }
}
