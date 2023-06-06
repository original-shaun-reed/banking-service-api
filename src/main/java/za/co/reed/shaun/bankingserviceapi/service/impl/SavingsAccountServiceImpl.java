package za.co.reed.shaun.bankingserviceapi.service.impl;

import org.springframework.stereotype.Service;
import za.co.reed.shaun.bankingserviceapi.entity.Account;
import za.co.reed.shaun.bankingserviceapi.entity.TransactionHistory;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.service.AccountService;
import za.co.reed.shaun.bankingserviceapi.service.SavingsAccountService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.TransactionType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.util.Date;
import java.util.Objects;

@Service
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final AccountRepository accountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final AccountService accountService;

    public SavingsAccountServiceImpl(AccountRepository accountRepository,
                                     TransactionHistoryRepository transactionHistoryRepository,
                                     AccountService accountService) {
        this.accountRepository = accountRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.accountService = accountService;
    }

    @Override
    public AccountResponse openSavingsAccount(SavingsAccountRequest request) {
        Account account = accountService.openSavingsAccount(request);
        return new AccountResponse(account.getAccountHolderName(), account.getAccountHolderSurname(), account.getAccountNumber(),
                account.getAccountType(), account.getAccountBalance(), null);
    }

    @Override
    public TransactionResponse withdrawalFromSavingsAccount(AccountWithdrawalRequest request) {
        Account account = accountRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (Objects.isNull(account)) {
            throw new BankingServiceException.AccountNotFoundException("Withdrawal Error: Account doesn't exist");
        }

        if (!AccountType.SAVINGS.name().equalsIgnoreCase(account.getAccountType())) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: The account is not a savings account");
        }

        Double previousAccountBalance = account.getAccountBalance();
        account = accountService.withdrawFromSavingsAccount(account, request.withdrawalAmount());

        accountRepository.save(account);
        transactionHistoryRepository.save(new TransactionHistory(AccountType.SAVINGS.name(), account.getAccountNumber(),
                null, previousAccountBalance, account.getAccountBalance(),
                null, null,TransactionType.WITHDRAWAL.name()));

        return new TransactionResponse(account.getAccountNumber(), AccountType.SAVINGS, previousAccountBalance,
                account.getAccountBalance(), null, null);
    }

    @Override
    public TransactionResponse depositToSavingsAccount(AccountDepositRequest request) {
        Account account = accountRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (Objects.isNull(account)) {
            throw new BankingServiceException.AccountNotFoundException("Withdrawal Error: Account doesn't exist");
        }

        if (!AccountType.SAVINGS.name().equalsIgnoreCase(account.getAccountType())) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: The account is not a savings account");
        }

        Double previousAccountBalance = account.getAccountBalance();
        account = accountService.depositIntoAccount(account, request.depositAmount());
        account.setUpdatedAt(new Date());

        accountRepository.save(account);
        transactionHistoryRepository.save(new TransactionHistory(AccountType.SAVINGS.name(), account.getAccountNumber(),
                account.getAccountNumber(), previousAccountBalance, account.getAccountBalance(),
                null, null, TransactionType.DEPOSIT.name()));

        return new TransactionResponse(account.getAccountNumber(), AccountType.SAVINGS, previousAccountBalance,
                account.getAccountBalance(), null, null);
    }
}
