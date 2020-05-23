package uk.nhs.digital.nhsconnect.nhais.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import uk.nhs.digital.nhsconnect.nhais.parse.EdifactParser;

@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    public EdifactParser edifactParser() {
        return new EdifactParser();
    }
}
