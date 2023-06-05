package za.co.reed.shaun.bankingserviceapi.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(Integer accountNumber, AccountType accountType, Double previousAccountBalance,
                                  Double accountBalance, Double overDraftBalance) { }
