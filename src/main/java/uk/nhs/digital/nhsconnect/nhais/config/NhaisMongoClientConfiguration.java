package uk.nhs.digital.nhsconnect.nhais.config;


import com.google.common.base.Strings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.util.List;

@Configuration
@Slf4j
public class NhaisMongoClientConfiguration extends AbstractMongoClientConfiguration {

    @Value("${nhais.mongodb.database}")
    private String database;

    @Value("${nhais.mongodb.uri}")
    private String uri;

    @Value("${nhais.mongodb.host}")
    private String host;

    @Value("${nhais.mongodb.port}")
    private String port;

    @Value("${nhais.mongodb.username}")
    private String username;

    @Value("${nhais.mongodb.password}")
    private String password;

    @Value("${nhais.mongodb.options}")
    private String options;

    @Value("${nhais.mongodb.autoIndexCreation}")
    private String autoIndexCreation;

    @Override
    public String getDatabaseName() {
        return this.database;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        LOGGER.info("Configuring mongo client settings...");
        builder.applyConnectionString(new ConnectionString(this.createConnectionString()));
    }

    @Override
    protected boolean autoIndexCreation() {
        LOGGER.info("Auto index creation is '{}'", this.autoIndexCreation);
        return Boolean.parseBoolean(this.autoIndexCreation);
    }

    private String createConnectionString() {
        LOGGER.info("Creating a connection string for mongo client settings...");
        if (!Strings.isNullOrEmpty(host)) {
            LOGGER.info("A value was provided from mongodb host. Generating a connection string from individual properties.");
            return createConnectionStringFromProperties();
        } else if (!Strings.isNullOrEmpty(uri)) {
            LOGGER.info("A mongodb connection string provided in spring.data.mongodb.uri and will be used to configure the database connection.");
            return uri;
        } else {
            LOGGER.error("Mongodb must be configured using a connection string or individual properties. Both uri and host are null or empty");
            throw new RuntimeException("Missing mongodb connection string and/or properties");
        }
    }

    private String createConnectionStringFromProperties() {
        String cs = "mongodb://";
        if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
            LOGGER.debug("Including a username and password in the mongo connection string");
            cs += username + ":" + password + "@";
        } else {
            LOGGER.info("No mongodb username or password is configured. Will use an anonymous connection.");
        }
        LOGGER.debug("The generated connection string will used host '{}' and port '{}'", host, port);
        cs += host + ":" + port;
        if (!Strings.isNullOrEmpty(options)) {
            LOGGER.debug("The generated connection will use use options '{}'", options);
            cs += "/?" + options;
        } else {
            LOGGER.warn("No options for the mongodb connection string were provided. If connecting to a cluster the driver may not work as expected.");
        }
        return cs;
    }

    @Override
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(
            new TransactionTypeWriteConverter(),
            new InboundTransactionTypeReadConverter(),
            new OutboundTransactionTypeReadConverter()
        ));
    }

    @WritingConverter
    static class TransactionTypeWriteConverter implements Converter<ReferenceTransactionType.TransactionType, String> {
        @Override
        public String convert(ReferenceTransactionType.TransactionType source) {
            return source.getAbbreviation();
        }
    }

    @ReadingConverter
    static class InboundTransactionTypeReadConverter implements Converter<String, ReferenceTransactionType.Inbound> {
        public ReferenceTransactionType.Inbound convert(String s) {
            return (ReferenceTransactionType.Inbound) ReferenceTransactionType.TransactionType.fromAbbreviation(s);
        }
    }

    @ReadingConverter
    static class OutboundTransactionTypeReadConverter implements Converter<String, ReferenceTransactionType.Outbound> {
        public ReferenceTransactionType.Outbound convert(String s) {
            return (ReferenceTransactionType.Outbound) ReferenceTransactionType.TransactionType.fromAbbreviation(s);
        }
    }
}
