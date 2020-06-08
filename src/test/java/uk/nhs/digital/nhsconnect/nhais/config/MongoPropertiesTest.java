package uk.nhs.digital.nhsconnect.nhais.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MongoPropertiesTest {

//    @Test
//    public void createConnectionString_usesUriValueIfPresentAndHostIsAbsent() {
//        MongoProperties mongoProperties = new MongoProperties();
//        mongoProperties.setUri("mongodb://localhost:27017");
//        assertThat(mongoProperties.createConnectionString())
//                .isEqualTo("mongodb://localhost:27017");
//    }
//
//    @Test
//    public void createConnectionString_usesPropertiesIfHostIsNotPresent() {
//        MongoProperties mongoProperties = new MongoProperties();
//        mongoProperties.setHost("mongohost");
//        mongoProperties.setPort("12345");
//        mongoProperties.setUsername("username");
//        mongoProperties.setPassword("password");
//        mongoProperties.setOptions("option1=value1&option2=value2");
//        assertThat(mongoProperties.createConnectionString())
//                .isEqualTo("mongodb://username:password@mongohost:12345?option1=value1&option2=value2");
//    }
//
//    @Test
//    public void createConnectionString_throwsExceptionIfUriAndHostAreAbsent() {
//        MongoProperties mongoProperties = new MongoProperties();
//        assertThatThrownBy(mongoProperties::createConnectionString)
//                .isInstanceOf(RuntimeException.class)
//                .hasMessage("Missing mongodb connection string and/or properties");
//    }

}
