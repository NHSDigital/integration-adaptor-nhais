package uk.nhs.digital.nhsconnect.nhais;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJms
@EnableScheduling
@SpringBootApplication
public class IntegrationAdaptorNhaisApplication {

	public static void main(String[] args) {
	    SpringApplication.run(IntegrationAdaptorNhaisApplication.class, args);
	}
}
