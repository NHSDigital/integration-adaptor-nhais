package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import uk.nhs.digital.nhsconnect.nhais.exceptions.EdifactValidationException;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Data
public class PersonName extends Segment {

    private @NonNull String nhsNumber;
    private @NonNull String surname;
    private String forename;
    private String title;
    private String middleName;
    private String[] otherNames;

    @Override
    public String getKey() {
        return "PNA";
    }

    @Override
    public String getValue() {
        String otherNamesConcatenated = otherNames == null ? StringUtils.EMPTY : String.join(" ", otherNames);

        return Stream.of(
                "PAT",
                StringUtils.isEmpty(nhsNumber) ? StringUtils.EMPTY : (nhsNumber + ":OPI"),
                "", // not used by design
                "", // not used by design
                "SU:" + surname,
                StringUtils.isEmpty(forename) ? StringUtils.EMPTY : ("FO:" + forename),
                StringUtils.isEmpty(title) ? StringUtils.EMPTY : ("TI:" + title),
                StringUtils.isEmpty(middleName) ? StringUtils.EMPTY : ("MI:" + middleName),
                StringUtils.isEmpty(otherNamesConcatenated) ? StringUtils.EMPTY : ("FS:" + otherNamesConcatenated))
                .map(x -> Optional.ofNullable(x).orElse(""))
                .collect(Collectors.joining("+"));
    }

    @Override
    protected void validateStateful() throws EdifactValidationException {
    }

    @Override
    public void preValidate() throws EdifactValidationException {
        if (Objects.isNull(surname) || surname.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }

        if (Objects.isNull(nhsNumber) || nhsNumber.isBlank()) {
            throw new EdifactValidationException(getKey() + ": Attribute identifier is required");
        }
    }
}
