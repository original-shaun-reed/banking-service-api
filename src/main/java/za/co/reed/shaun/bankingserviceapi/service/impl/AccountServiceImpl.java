package za.co.reed.shaun.bankingserviceapi.service.impl;

import org.springframework.stereotype.Service;
import za.co.reed.shaun.bankingserviceapi.entity.Account;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;
import za.co.reed.shaun.bankingserviceapi.repository.AccountRepository;
import za.co.reed.shaun.bankingserviceapi.service.AccountService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.BankServiceConstants;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account openCurrentAccount(CurrentAccountRequest request) {
        return createAccount(new Account(request), request.accountNumber(), request.accountType());
    }

    @Override
    public Account openSavingsAccount(SavingsAccountRequest request) {
        return createAccount(new Account(request), request.accountNumber(), request.accountType());
    }

    private Account createAccount(Account account, Integer accountNumber, AccountType accountType) {
        if (Boolean.FALSE.equals(AccountType.doesAccountExist(accountType))) {
            throw new BankingServiceException.AccountTypeNotFoundException("Invalid account type provided");
        }

        Account existingAccount = accountRepository.getAccountInformationByAccountNumber(accountNumber);

        if (!Objects.isNull(existingAccount)) {
            throw new BankingServiceException.AccountExistsException("Account already exists for given account number");
        }

        return accountRepository.save(account);
    }

    @Override
    public Account depositIntoAccount(Account account, Double depositAmount) {
        // Calculating new savings account balance to be updated
        Double newAccountBalance = BigDecimal.valueOf(account.getAccountBalance())
                .add(BigDecimal.valueOf(depositAmount)).doubleValue();

        account.setAccountBalance(newAccountBalance);

        return account;
    }

    @Override
    public Account withdrawFromCurrentAccount(Account account, Double withdrawalAmount) {
        Double previousAccountBalance = account.getAccountBalance();

        //Checking if the account balance is not zero, if zero, we will then transfer money from the overdraft account
        if (previousAccountBalance > BigDecimal.ZERO.doubleValue()) {
            Double newAccountBalance = BigDecimal.valueOf(previousAccountBalance)
                    .subtract(BigDecimal.valueOf(withdrawalAmount)).doubleValue();

            // This will carry the remainder over from the transfer amount
            // e.g. if the account balance was 500 amd the transfer amount is 1000 then -500 (which is the remainder) will be added to overdraft balance
            if (newAccountBalance < BigDecimal.ZERO.doubleValue()) {
                account = withdrawalFromOverdraftOnCurrentAccount(account, newAccountBalance);
            } else {
                account.setAccountBalance(newAccountBalance);
            }
        } else {
            account = withdrawalFromOverdraftOnCurrentAccount(account, withdrawalAmount);
        }

        return account;
    }

    @Override
    public Account withdrawFromSavingsAccount(Account account, Double withdrawalAmount) {
        Double previousAccountBalance = account.getAccountBalance();

        if (previousAccountBalance < withdrawalAmount) {
            throw new BankingServiceException.AccountTransactionException("Amount exceeds the available balance");
        }

        if (previousAccountBalance <= BankServiceConstants.minimumSavingsAccountBalance) {
            throw new BankingServiceException.AccountTransactionException("Cannot exceed minimum account balance");
        }

        Double accountBalanceAfterTransfer = BigDecimal.valueOf(previousAccountBalance)
                .subtract(BigDecimal.valueOf(withdrawalAmount)).doubleValue();

        if (accountBalanceAfterTransfer < BankServiceConstants.minimumSavingsAccountBalance) {
            throw new BankingServiceException.AccountTransactionException("The amount selected is too high");
        }

        account.setAccountBalance(accountBalanceAfterTransfer);
        return account;
    }

    private Account withdrawalFromOverdraftOnCurrentAccount(Account account, Double withdrawalAmount) {
        Double overdraft = determineOverdraftAmount(account.getOverdraftBalance(), withdrawalAmount);

        if (overdraft >= BankServiceConstants.overdraftMaxAmount) {
            account.setAccountBalance(BigDecimal.ZERO.doubleValue());
            account.setOverdraftBalance(overdraft);

            return account;
        }

        throw new BankingServiceException.AccountTransactionException("No funds available in account and/or exceeds overdraft amount");
    }

    private Double determineOverdraftAmount(Double overdraftBalance, Double transferAmount) {
        if (transferAmount < BigDecimal.ZERO.doubleValue()) {
            return BigDecimal.valueOf(overdraftBalance).add(BigDecimal.valueOf(transferAmount)).doubleValue();
        }

        return BigDecimal.valueOf(overdraftBalance).subtract(BigDecimal.valueOf(transferAmount)).doubleValue();
    }
}
