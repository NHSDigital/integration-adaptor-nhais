package uk.nhs.digital.nhsconnect.nhais.outbound;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String KEY = "CorrelationId";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
        throws java.io.IOException, ServletException {
        try {
            var token = request.getHeader(KEY);
            if (StringUtils.isEmpty(token)) {
                token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            }
            MDC.put(KEY, token);
            response.addHeader(KEY, token);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(KEY);
        }
    }
}
