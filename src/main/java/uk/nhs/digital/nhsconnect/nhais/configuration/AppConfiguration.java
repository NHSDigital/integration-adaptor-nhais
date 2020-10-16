package uk.nhs.digital.nhsconnect.nhais.configuration;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nhais")
@Getter
@Setter
@Slf4j
public class AppConfiguration {

    private String trustStorePath;
    private String trustStorePassword;

    @Bean
    public AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider("default"))
            .withRegion(Regions.EU_WEST_2)
            .build();
    }
}
