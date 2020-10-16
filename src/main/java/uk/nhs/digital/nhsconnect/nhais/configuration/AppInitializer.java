package uk.nhs.digital.nhsconnect.nhais.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AppInitializer implements InitializingBean {

    private final AppConfiguration appConfiguration;
    private final CustomTrustStore customTrustStore;

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isNotBlank(appConfiguration.getTrustStorePath())) {
            LOGGER.info("Adding custom TrustStore to default one");
            customTrustStore.addToDefault(
                appConfiguration.getTrustStorePath(), appConfiguration.getTrustStorePassword());
        }
    }
}
