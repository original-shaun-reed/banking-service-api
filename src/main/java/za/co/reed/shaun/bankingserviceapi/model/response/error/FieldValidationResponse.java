package za.co.reed.shaun.bankingserviceapi.model.response.error;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FieldValidationResponse {
    private ArrayList<FieldValidationErrorDetails> errors;
}
