package za.co.reed.shaun.bankingserviceapi.service;

import za.co.reed.shaun.bankingserviceapi.entity.AccountInformation;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;

public interface AccountService {
    AccountInformation openAccount(CurrentAccountRequest request);
    AccountInformation retrieveAccountInformation(Integer accountNumber);
}
