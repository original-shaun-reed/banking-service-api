package za.co.reed.shaun.bankingserviceapi.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TransferResponse(String message, @JsonIgnore Double updatedToAmount,
                               @JsonIgnore Double updatedOverdraftAmount) {
}
