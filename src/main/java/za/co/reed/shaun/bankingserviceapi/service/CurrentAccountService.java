package za.co.reed.shaun.bankingserviceapi.service;

import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;

public interface CurrentAccountService {
    AccountResponse openCurrentAccount(CurrentAccountRequest request);
    TransactionResponse withdrawalFromCurrentAccount(AccountWithdrawalRequest request);
    TransactionResponse depositToCurrentAccount(AccountDepositRequest request);
}
