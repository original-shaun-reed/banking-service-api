package za.co.reed.shaun.bankingserviceapi.service;

import za.co.reed.shaun.bankingserviceapi.entity.Account;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;

public interface AccountService {
    Account openCurrentAccount(CurrentAccountRequest request);
    Account openSavingsAccount(SavingsAccountRequest request);
    Account depositIntoAccount(Account account, Double depositAmount);
    Account withdrawFromCurrentAccount(Account account, Double withdrawalAmount);
    Account withdrawFromSavingsAccount(Account account, Double withdrawalAmount);
}
