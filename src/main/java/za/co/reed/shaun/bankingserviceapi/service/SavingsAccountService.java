package za.co.reed.shaun.bankingserviceapi.service;

import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;

public interface SavingsAccountService {
    AccountResponse openSavingsAccount(SavingsAccountRequest request);
    TransactionResponse withdrawalFromSavingsAccount(AccountWithdrawalRequest request);
    TransactionResponse depositToSavingsAccount(AccountDepositRequest request);
}
