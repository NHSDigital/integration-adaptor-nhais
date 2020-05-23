package uk.nhs.digital.nhsconnect.nhais.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.mesh.MeshMessage;

@Component @Slf4j
public class RecepConsumerService {

    public void handleRecep(MeshMessage meshMessage) {
        LOGGER.info("Consumed RECEP message: {}", meshMessage);
    }

}
