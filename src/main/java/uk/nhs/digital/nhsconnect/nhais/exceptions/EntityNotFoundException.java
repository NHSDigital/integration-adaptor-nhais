package uk.nhs.digital.nhsconnect.nhais.exceptions;

import org.springframework.dao.DataRetrievalFailureException;

public class EntityNotFoundException extends DataRetrievalFailureException {
    private static final String MESSAGE = "Following query '%s' yield no result";

    public EntityNotFoundException(String query) {
        super(String.format(MESSAGE, query));
    }
}
