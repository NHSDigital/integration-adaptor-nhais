package uk.nhs.digital.nhsconnect.nhais.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.digital.nhsconnect.nhais.outbound.ConversationIdFilter;

@Configuration
public class ConversationIdFilterConfig {

    @Bean
    public FilterRegistrationBean<ConversationIdFilter> servletRegistrationBean() {
        final FilterRegistrationBean<ConversationIdFilter> registrationBean = new FilterRegistrationBean<>();
        final ConversationIdFilter conversationIdFilter = new ConversationIdFilter();
        registrationBean.setFilter(conversationIdFilter);
        registrationBean.setOrder(2);
        return registrationBean;
    }
}