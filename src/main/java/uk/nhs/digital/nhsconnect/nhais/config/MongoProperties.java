package uk.nhs.digital.nhsconnect.nhais.config;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "nhais.mongodb")
@Slf4j
public class MongoProperties {

    private String database;

    private String uri;

    private String host;

    private String port;

    private String username;

    private String password;

    private String options;

    private boolean autoIndexCreation;

    public String createConnectionString() {
        if(!Strings.isNullOrEmpty(host)) {
            LOGGER.info("A value was provided from mongodb host. Generating a connection string from individual properties.");
            return createConnectionStringFromProperties();
        } else if(!Strings.isNullOrEmpty(uri)) {
            LOGGER.info("A mongodb connection string provided in spring.data.mongodb.uri and will be used to configure the database connection.");
            return uri;
        } else {
            LOGGER.error("Mongodb must be configured using a connection string or individual properties. Both uri and host are null or empty");
            throw new RuntimeException("Missing mongodb connection string and/or properties");
        }
    }

    private String createConnectionStringFromProperties() {
        String cs = "mongodb://";
        if(!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
            cs += username + ":" + password + "@";
        } else {
            LOGGER.info("No mongodb username or password is configured. Will use an anonymous connection.");
        }
        cs += host + ":" + port;
        if(!Strings.isNullOrEmpty(options)) {
            cs += "?" + options;
        } else {
            LOGGER.warn("No options for the mongodb connection string were provided. If connecting to a cluster the driver may not work as expected.");
        }
        return cs;
    }

}
