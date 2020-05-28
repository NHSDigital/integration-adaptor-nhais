package uk.nhs.digital.nhsconnect.nhais.config;


import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.util.StringUtils;

import static java.util.Collections.singletonList;

@Configuration
@Slf4j
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${nhais.mongo.databaseName}")
    String databaseName;

    @Value("${nhais.mongo.username}")
    String username;

    @Value("${nhais.mongo.password}")
    String password;

    @Value("${nhais.mongo.host}")
    String host;

    @Value("${nhais.mongo.port}")
    int port;

    @Value("${nhais.mongo.autoIndexCreation:false}")
    boolean autoIndexCreation;

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        if(!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)) {
            builder.credential(MongoCredential.createCredential(username, databaseName, password.toCharArray()));
        }
        builder.applyToClusterSettings(settings -> settings.hosts(singletonList(new ServerAddress(host, port))));
    }

    @Override
    @Value("${nhais.mongo.autoIndexCreation}")
    protected boolean autoIndexCreation() {
        LOGGER.info("Auto index creation is {}", autoIndexCreation);
        return autoIndexCreation;
    }
}
