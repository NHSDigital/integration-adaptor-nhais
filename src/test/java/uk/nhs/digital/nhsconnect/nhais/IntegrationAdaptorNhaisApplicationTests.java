package uk.nhs.digital.nhsconnect.nhais;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"nhais.mongodb.autoIndexCreation=false" // no mongodb instance to create indexes at startup
})
class IntegrationAdaptorNhaisApplicationTests {

	@Test
	void when_databaseIsNotReachable_then_applicationStartsUpWithNegativeHealthcheck() {
	}

}
