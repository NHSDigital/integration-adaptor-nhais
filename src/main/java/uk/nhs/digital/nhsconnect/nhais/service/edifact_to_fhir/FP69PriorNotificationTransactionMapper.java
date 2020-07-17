package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FP69ExpiryDate;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FP69ReasonCode;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FreeText;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonAddress;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;
import uk.nhs.digital.nhsconnect.nhais.parse.NullableStringType;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FP69PriorNotificationTransactionMapper implements FhirTransactionMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void map(Parameters parameters, Transaction transaction) {
        var patient = ParametersExtension.extractPatient(parameters);
        mapNHSNumber(patient, transaction);
        mapName(patient, transaction);
        mapDateOfBirth(patient, transaction);
        mapAddress(patient, transaction);

        mapReasonCode(parameters, transaction);
        mapExpiryDate(parameters, transaction);
        mapFreeText(parameters, transaction);
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.FP69_PRIOR_NOTIFICATION;
    }

    private void mapNHSNumber(Patient patient, Transaction transaction) {
        transaction.getPersonName()
            .map(PersonName::getNhsNumber)
            .filter(StringUtils::isNotBlank)
            .map(NhsIdentifier::new)
            .map(Identifier.class::cast)
            .map(List::of)
            .ifPresentOrElse(
                patient::setIdentifier,
                () -> {
                    throw new EdifactValidationException(
                        "For an FP69 prior notification (reference F9) the PNA+PAT segment is required to provide the patient NHS number");
                }
            );
    }

    private void mapName(Patient patient, Transaction transaction) {
        transaction.getPersonName()
            .ifPresentOrElse(
                personName -> {
                    var humanName = patient.addName();
                    mapSurname(personName, humanName);
                    mapForenames(personName, humanName);
                    mapTitle(personName, humanName);
                },
                () -> {
                    throw new EdifactValidationException(
                        "For an FP69 prior notification (reference F9) the PNA+PAT segment is required");
                });
    }

    private void mapDateOfBirth(Patient patient, Transaction transaction) {
        transaction.getPersonDateOfBirth()
            .map(PersonDateOfBirth::getDateOfBirth)
            .map(expiryDate -> expiryDate.format(DATE_FORMAT))
            .map(Date::valueOf)
            .ifPresentOrElse(
                patient::setBirthDate,
                () -> {
                    throw new EdifactValidationException(
                        "For an FP69 prior notification (reference F9) the DTM+329 segment is required to provide the patient date of birth");
                });
    }

    private void mapAddress(Patient patient, Transaction transaction) {
        transaction.getPersonAddress()
            .ifPresent(personAddress -> {
                var address = patient.addAddress();
                mapPostalCode(personAddress, address);
                mapAddressLines(personAddress, address);
            });
    }

    private void mapReasonCode(Parameters parameters, Transaction transaction) {
        transaction.getFp69ReasonCode()
            .map(FP69ReasonCode::getCode)
            .map(Object::toString)
            .map(StringType::new)
            .map(reasonCode -> new Parameters.ParametersParameterComponent()
                .setName(ParameterNames.FP69_REASON_CODE)
                .setValue(reasonCode))
            .ifPresentOrElse(
                parameters::addParameter,
                () -> {
                    throw new EdifactValidationException(
                        "For an FP69 prior notification (reference F9) the HEA+FRN segment is required");
                });
    }

    private void mapExpiryDate(Parameters parameters, Transaction transaction) {
        transaction.getFp69ExpiryDate()
            .map(FP69ExpiryDate::getExpiryDate)
            .map(expiryDate -> expiryDate.format(DATE_FORMAT))
            .map(StringType::new)
            .map(expiryDate -> new Parameters.ParametersParameterComponent()
                .setName(ParameterNames.FP69_EXPIRY_DATE)
                .setValue(expiryDate))
            .ifPresentOrElse(
                parameters::addParameter,
                () -> {
                    throw new EdifactValidationException(
                        "For an FP69 prior notification (reference F9) the DTM+962 segment is required");
                });
    }

    private void mapFreeText(Parameters parameters, Transaction transaction) {
        transaction.getFreeText()
            .map(FreeText::getFreeTextValue)
            .map(StringType::new)
            .map(text -> new Parameters.ParametersParameterComponent()
                .setName(ParameterNames.FREE_TEXT)
                .setValue(text))
            .ifPresent(parameters::addParameter);
    }

    private void mapAddressLines(PersonAddress personAddress, Address address) {
        address.setLine(Stream.of(
            Optional.ofNullable(personAddress.getAddressLine1()),
            Optional.ofNullable(personAddress.getAddressLine2()),
            Optional.ofNullable(personAddress.getAddressLine3()),
            Optional.ofNullable(personAddress.getAddressLine4()),
            Optional.ofNullable(personAddress.getAddressLine5()))
            .map(line -> new NullableStringType(line.orElse(null)))
            .map(StringType.class::cast)
            .collect(Collectors.toList()));
    }

    private void mapPostalCode(PersonAddress personAddress, Address address) {
        Optional.ofNullable(personAddress.getPostalCode())
            .filter(StringUtils::isNotBlank)
            .ifPresent(address::setPostalCode);
    }

    private void mapTitle(PersonName personName, HumanName humanName) {
        Optional.ofNullable(personName.getTitle())
            .map(StringType::new)
            .map(List::of)
            .ifPresent(humanName::setPrefix);
    }

    private void mapForenames(PersonName personName, HumanName humanName) {
        Stream.of(
            Optional.ofNullable(personName.getFirstForename()),
            Optional.ofNullable(personName.getSecondForename()),
            Optional.ofNullable(personName.getOtherForenames()))
            .flatMap(Optional::stream)
            .forEach(humanName::addGiven);
    }

    private void mapSurname(PersonName personName, HumanName humanName) {
        Optional.ofNullable(personName.getSurname())
            .ifPresentOrElse(
                humanName::setFamily,
                () -> {
                    throw new EdifactValidationException(
                        "For an FP69 prior notification (reference F9) the PNA+PAT segment is required to provide the patient surname");
                });
    }
}
