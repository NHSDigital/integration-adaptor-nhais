package uk.nhs.digital.nhsconnect.nhais.config;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
@Slf4j
public class NhaisMongoClientConfiguration extends AbstractMongoClientConfiguration {

    @Autowired
    private MongoProperties mongoProperties;

    @Override
    public String getDatabaseName() {
        return mongoProperties.getDatabase();
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(new ConnectionString(mongoProperties.createConnectionString()));
    }

    @Override
    protected boolean autoIndexCreation() {
        LOGGER.info("Auto index creation is {}", mongoProperties.isAutoIndexCreation());
        return mongoProperties.isAutoIndexCreation();
    }


}
