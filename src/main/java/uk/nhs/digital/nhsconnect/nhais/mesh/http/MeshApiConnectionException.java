package uk.nhs.digital.nhsconnect.nhais.mesh.http;

import org.springframework.http.HttpStatus;
import uk.nhs.digital.nhsconnect.nhais.rest.exception.NhaisBaseException;

public class MeshApiConnectionException extends NhaisBaseException {

    public MeshApiConnectionException(String description, HttpStatus expectedStatus, HttpStatus actualStatus) {
        super(description + " Expected status code: " + expectedStatus.value() + ", but received: " + actualStatus.value());
    }

    public MeshApiConnectionException(String description, HttpStatus expectedStatus, HttpStatus actualStatus, String content) {
        super(description + " Expected status code: " + expectedStatus.value() + ", but received: " + actualStatus.value() + " with response content\n" + content);
    }

    public MeshApiConnectionException(String message) {
        super(message);
    }
}
