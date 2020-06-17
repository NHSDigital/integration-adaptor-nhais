package uk.nhs.digital.nhsconnect.nhais.service.edifact_to_fhir;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.ReferenceTransactionType;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class Config {
    @Bean
    public Map<ReferenceTransactionType.TransactionType, TransactionMapper> getTransactionMappers(Set<TransactionMapper> transactionMappers) {
        return transactionMappers.stream()
            .collect(Collectors.toMap(TransactionMapper::getTransactionType, Function.identity()));
    }
}
