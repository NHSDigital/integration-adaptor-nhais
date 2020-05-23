package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

@Component @Slf4j
public class RegistrationConsumerService {

    public void handleRegistration(MeshMessage meshMessage) {
        LOGGER.info("Consumed Registration message: {}", meshMessage);
    }

}
