package za.co.reed.shaun.bankingserviceapi.service.impl;

import org.springframework.stereotype.Service;
import za.co.reed.shaun.bankingserviceapi.entity.Account;
import za.co.reed.shaun.bankingserviceapi.entity.TransactionHistory;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.service.AccountService;
import za.co.reed.shaun.bankingserviceapi.service.CurrentAccountService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.TransactionType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.util.Date;
import java.util.Objects;

@Service
public class CurrentAccountServiceImpl implements CurrentAccountService {
    private final AccountRepository accountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final AccountService accountService;

    public CurrentAccountServiceImpl(AccountRepository accountRepository,
                                     TransactionHistoryRepository transactionHistoryRepository,
                                     AccountService accountService) {
        this.accountRepository = accountRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.accountService = accountService;
    }

    @Override
    public AccountResponse openCurrentAccount(CurrentAccountRequest request) {
        Account account = accountService.openCurrentAccount(request);

        return new AccountResponse(account.getAccountHolderName(), account.getAccountHolderSurname(), account.getAccountNumber(),
                account.getAccountType(), account.getAccountBalance(), account.getOverdraftBalance());
    }

    @Override
    public TransactionResponse withdrawalFromCurrentAccount(AccountWithdrawalRequest request) {
        Account account = accountRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (Objects.isNull(account)) {
            throw new BankingServiceException.AccountNotFoundException("Withdrawal Error: Account doesn't exist");
        }

        if (!AccountType.CURRENT.name().equalsIgnoreCase(account.getAccountType())) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: The account is not a current account");
        }

        Double previousAccountBalance = account.getAccountBalance();

        account = accountService.withdrawFromCurrentAccount(account, request.withdrawalAmount());
        account.setUpdatedAt(new Date());

        accountRepository.save(account);
        transactionHistoryRepository.save(new TransactionHistory(AccountType.CURRENT.name(), account.getAccountNumber(),
                null, previousAccountBalance, account.getAccountBalance(),
                account.getAccountBalance(), account.getOverdraftBalance(),TransactionType.WITHDRAWAL.name()));

        return new TransactionResponse(account.getAccountNumber(), AccountType.CURRENT, previousAccountBalance,
                account.getAccountBalance(), account.getOverdraftBalance(), account.getOverdraftBalance());
    }

    @Override
    public TransactionResponse depositToCurrentAccount(AccountDepositRequest request) {
        Account account = accountRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (Objects.isNull(account)) {
            throw new BankingServiceException.AccountNotFoundException("Deposit Error: Account doesn't exist");
        }

        if (!AccountType.CURRENT.name().equals(account.getAccountType())) {
            throw new BankingServiceException.AccountTransactionException("Deposit Error: The account is not a current account");
        }

        Double previousAccountBalance = account.getAccountBalance();
        Double previousOverdraftAccountBalance = account.getOverdraftBalance();

        account = accountService.depositIntoAccount(account, request.depositAmount());
        account.setUpdatedAt(new Date());

        accountRepository.save(account);
        transactionHistoryRepository.save(new TransactionHistory(AccountType.CURRENT.name(), account.getAccountNumber(),
                account.getAccountNumber(), previousAccountBalance, account.getAccountBalance(),
                previousOverdraftAccountBalance, account.getOverdraftBalance(), TransactionType.DEPOSIT.name()));

        return new TransactionResponse(account.getAccountNumber(), AccountType.CURRENT, previousAccountBalance,
                account.getAccountBalance(), previousOverdraftAccountBalance, account.getOverdraftBalance());
    }
}
