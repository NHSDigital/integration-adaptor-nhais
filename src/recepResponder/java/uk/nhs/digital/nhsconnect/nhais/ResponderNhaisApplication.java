package uk.nhs.digital.nhsconnect.nhais;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJms
@EnableScheduling
@SpringBootApplication
public class ResponderNhaisApplication {

	public static void main(String[] args) {
	    SpringApplication.run(ResponderNhaisApplication.class, args);
	}

	@Configuration
	class Config {

		@Bean
		String registrationConsumerService() {
			return uk.nhs.digital.nhsconnect.nhais.responder.RecepResponderService.class.getSimpleName();
		}
	}
}
