package uk.nhs.digital.nhsconnect.nhais.inbound.mapper;

import org.hl7.fhir.r4.model.Parameters;
import org.springframework.stereotype.Component;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionDate;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.DeductionReasonCode;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.NewHealthAuthorityName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.PersonName;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.Transaction;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.EdifactValidationException;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.NhsIdentifier;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParameterNames;
import uk.nhs.digital.nhsconnect.nhais.model.fhir.ParametersExtension;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DeductionTransactionMapper implements FhirTransactionMapper {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void map(Parameters parameters, Transaction transaction) {
        transaction.getPersonName()
            .map(PersonName::getNhsNumber)
            .map(NhsIdentifier::new)
            .ifPresentOrElse(nhsIdentifier -> ParametersExtension.extractPatient(parameters).setIdentifier(List.of(nhsIdentifier)),
                () -> {throw new EdifactValidationException("Missing mandatory patient NHS number");});

        transaction.getDeductionReasonCode()
            .map(DeductionReasonCode::getCode)
            .ifPresentOrElse(code -> parameters.addParameter(ParameterNames.DEDUCTION_REASON_CODE, code),
                () -> {throw new EdifactValidationException("Missing mandatory deduction reason code");});

        transaction.getDeductionDate()
            .map(DeductionDate::getDate)
            .ifPresentOrElse(deductionDate -> parameters.addParameter(ParameterNames.DATE_OF_DEDUCTION, deductionDate.format(DATE_TIME_FORMATTER)),
                () -> {throw new EdifactValidationException("Missing mandatory date of deduction");});

        transaction.getNewHealthAuthorityName()
            .map(NewHealthAuthorityName::getHaName)
            .ifPresent(haName -> parameters.addParameter(ParameterNames.NEW_HA_CIPHER, haName));
    }

    @Override
    public ReferenceTransactionType.TransactionType getTransactionType() {
        return ReferenceTransactionType.Inbound.DEDUCTION;
    }
}
