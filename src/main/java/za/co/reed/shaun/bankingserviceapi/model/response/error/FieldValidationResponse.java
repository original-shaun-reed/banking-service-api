package za.co.reed.shaun.bankingserviceapi.model.response.error;

import lombok.Data;
import za.co.reed.shaun.bankingserviceapi.model.response.error.FieldValidationErrorDetails;

import java.util.ArrayList;

@Data
public class FieldValidationResponse {
    private ArrayList<FieldValidationErrorDetails> errors;
}
