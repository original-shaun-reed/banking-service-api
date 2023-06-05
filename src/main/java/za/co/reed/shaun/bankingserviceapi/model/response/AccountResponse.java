package za.co.reed.shaun.bankingserviceapi.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountResponse(String accountHolderName, String accountHolderSurname, Integer accountNumber,
                              String accountType, Double accountBalance, Double overDraftBalance) implements Serializable {}
