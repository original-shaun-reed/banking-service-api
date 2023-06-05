package za.co.reed.shaun.bankingserviceapi.model.response.error;

import lombok.Data;

@Data
public class FieldValidationErrorDetails {
    private String fieldName;
    private String message;
}
