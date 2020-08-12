package uk.nhs.digital.nhsconnect.nhais.pcrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication(exclude = {
	MongoAutoConfiguration.class,
	MongoDataAutoConfiguration.class
})
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}
}
