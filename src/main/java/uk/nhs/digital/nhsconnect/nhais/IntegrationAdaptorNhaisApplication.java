package uk.nhs.digital.nhsconnect.nhais;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class IntegrationAdaptorNhaisApplication {

	public static void main(String[] args) {
	    SpringApplication.run(IntegrationAdaptorNhaisApplication.class, args);
	}
}
