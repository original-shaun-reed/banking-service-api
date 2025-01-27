package za.co.reed.shaun.bankingserviceapi.model.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorHandlingResponse {
    String message;
    String path;
}
