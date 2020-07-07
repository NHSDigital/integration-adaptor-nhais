package uk.nhs.digital.nhsconnect.nhais.utils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import uk.nhs.digital.nhsconnect.nhais.exceptions.FhirValidationException;

import org.junit.jupiter.api.Test;

class MissingOrEmptyElementUtilsTest {

    @Test
    public void whenNullObject_theThrowsFhirValidationException() {
        String path = "any.path";
        Object object = null;
        assertThatThrownBy(() -> MissingOrEmptyElementUtils.exceptionIfMissingOrEmpty(path, object))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void whenEmptyList_thenThrowsFhirValidationException() {
        String path = "any.path";
        List<Object> objectList = Collections.emptyList();
        assertThatThrownBy(() -> MissingOrEmptyElementUtils.exceptionIfMissingOrEmpty(path, objectList))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

    @Test
    public void whenBlankString_thenThrowsFhirValidationException() {
        String path = "any.path";
        String emptyString = "";
        assertThatThrownBy(() -> MissingOrEmptyElementUtils.exceptionIfMissingOrEmpty(path, emptyString))
            .isExactlyInstanceOf(FhirValidationException.class);
    }

}