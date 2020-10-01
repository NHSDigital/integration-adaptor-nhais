package uk.nhs.digital.nhsconnect.nhais.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.digital.nhsconnect.nhais.outbound.CorrelationIdFilter;

@Configuration
public class CorrelationIdFilterConfig {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> servletRegistrationBean() {
        final FilterRegistrationBean<CorrelationIdFilter> registrationBean = new FilterRegistrationBean<>();
        final CorrelationIdFilter correlationIdFilter = new CorrelationIdFilter();
        registrationBean.setFilter(correlationIdFilter);
        registrationBean.setOrder(2);
        return registrationBean;
    }
}