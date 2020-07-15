package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.FP69ReasonCode;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonDateOfBirth;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.nhs.digital.nhsconnect.nhais.model.edifact.Segment.removeEmptyTrailingFields;

@Component
public class FP69PriorNotificationTransactionMapper implements FhirTransactionMapper {
    @Override
    public void map(Parameters parameters, Transaction transaction) {
        mapReasonCode(parameters, transaction);
        mapExpiryDate(parameters, transaction);

        var patient = ParametersExtension.extractPatient(parameters);
        mapNHSNumber(patient, transaction);
        mapName(patient, transaction);
        mapBirthDate(patient, transaction);
        mapAddress(patient, transaction);
    }

    private void mapAddress(Patient patient, Transaction transaction) {
        transaction.getPersonAddress()
            .ifPresent(personAddress -> {
                var anythingWasSet = new AtomicBoolean(false);
                var address = new Address();

                Optional.ofNullable(personAddress.getPostalCode())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(postalCode -> {
                        anythingWasSet.set(true);
                        address.setPostalCode(personAddress.getPostalCode());
                    });

                var addressLines = Stream.of(
                    Optional.ofNullable(personAddress.getAddressLine1()),
                    Optional.ofNullable(personAddress.getAddressLine2()),
                    Optional.ofNullable(personAddress.getAddressLine3()),
                    Optional.ofNullable(personAddress.getAddressLine4()))
//                    .map(name -> name.orElse(StringUtils.EMPTY))
                    .map(x -> new StringType(x.orElse(null)))
                    .collect(Collectors.toList());
                if (!addressLines.isEmpty()) {
                    anythingWasSet.set(true);
                    address.setLine(addressLines);
                }

                if (anythingWasSet.get()) {
                    patient.setAddress(List.of(address));
                }
            });
    }

    private void mapBirthDate(Patient patient, Transaction transaction) {
        //TODO: is this optional?
        transaction.getPersonDateOfBirth()
            .map(PersonDateOfBirth::getTimestamp)
            .map(Date::from)
            .ifPresent(patient::setBirthDate);
    }

    private void mapName(Patient patient, Transaction transaction) {
        transaction.getPersonName()
            .ifPresent(personName -> {
                var anythingWasSet = new AtomicBoolean(false);
                var humanName = new HumanName();

                var names = Stream.of(
                        Optional.ofNullable(personName.getForename()),
                        Optional.ofNullable(personName.getMiddleName()),
                        Optional.ofNullable(personName.getThirdForename()))
                    .map(name -> name.orElse(StringUtils.EMPTY))
                    .map(StringType::new)
                    .collect(Collectors.toList());
                names = removeEmptyTrailingFields(names, stringType -> StringUtils.isNotBlank(stringType.getValue()));
                if (!names.isEmpty()) {
                    anythingWasSet.set(true);
                    humanName.setGiven(names);
                }

                Optional.ofNullable(personName.getTitle())
                    .ifPresent(title -> {
                        anythingWasSet.set(true);
                        humanName.setPrefix(List.of(new StringType(title)));
                    });

                Optional.ofNullable(personName.getFamilyName())
                    .ifPresent(family -> {
                        anythingWasSet.set(true);
                        humanName.setFamily(family);
                    });

                if (anythingWasSet.get()) {
                    patient.setName(List.of(humanName));
                }
            });
    }

    private void mapNHSNumber(Patient patient, Transaction transaction) {
        transaction.getPersonName()
            .map(PersonName::getNhsNumber)
            .flatMap(this::mapToNhsIdentifier)
            .ifPresent(nhsIdentifier -> patient.setIdentifier(List.of(nhsIdentifier)));
    }

    private Optional<NhsIdentifier> mapToNhsIdentifier(String nhsNumber) {
        return Optional.ofNullable(nhsNumber).map(NhsIdentifier::new);
    }

    private void mapReasonCode(Parameters parameters, Transaction transaction) {
        var reasonCode = transaction.getFp69ReasonCode()
            .orElseThrow(() -> new EdifactValidationException(String.format(
                "Segment %s must be present for %s inbound transaction",
                FP69ReasonCode.KEY_QUALIFIER, getTransactionType().getCode())))
            .getCode();

        parameters.addParameter()
            .setName(ParameterNames.FP69_REASON_CODE)
            .setValue(new StringType(reasonCode.toString()));
    }

    private void mapExpiryDate(Parameters parameters, Transaction transaction) {
        var timestamp = transaction.getFp69ExpiryDate().getTimestamp();

        parameters.addParameter()
            .setName(ParameterNames.FP69_EXPIRY_DATE)
            .setValue(new DateType(Date.from(timestamp))); //TODO: format date
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.FP69_PRIOR_NOTIFICATION;
    }
}
