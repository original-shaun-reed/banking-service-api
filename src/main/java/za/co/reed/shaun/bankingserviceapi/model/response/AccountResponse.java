package za.co.reed.shaun.bankingserviceapi.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountResponse(String accountHolderName, String accountHolderSurname, Integer accountNumber,
                              String accountType, Double accountBalance, Double overDraftBalance) {}
