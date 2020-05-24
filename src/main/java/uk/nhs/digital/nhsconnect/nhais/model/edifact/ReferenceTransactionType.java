package uk.nhs.digital.nhsconnect.nhais.model.edifact;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 *class declaration:
 */
@Getter @Setter
public class ReferenceTransactionType extends Reference {

    public ReferenceTransactionType(@NonNull String qualifier, @NonNull String reference) {
        super("950", reference);
    }

    private String getTransactionType(String reference){
        String a;
        switch (reference){
            case "G1":
                return "ACCEPTANCE";
            case "G2":
                return "AMENDMENT";
            case "G3":
                return "REMOVAL";
            case "G4":
                return "DEDUCTION";
            default:
                return ""; //Should this raise exception?
        }
    }

}
