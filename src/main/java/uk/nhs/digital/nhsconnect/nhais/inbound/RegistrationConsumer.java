package uk.nhs.digital.nhsconnect.nhais.inbound;

import uk.nhs.digital.nhsconnect.nhais.mesh.message.InboundMeshMessage;

public interface RegistrationConsumer {
    void handleRegistration(InboundMeshMessage meshMessage);
}
