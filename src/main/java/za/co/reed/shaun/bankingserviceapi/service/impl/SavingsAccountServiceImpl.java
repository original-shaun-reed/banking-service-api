package za.co.reed.shaun.bankingserviceapi.service.impl;

import org.springframework.stereotype.Service;
import za.co.reed.shaun.bankingserviceapi.entity.AccountInformation;
import za.co.reed.shaun.bankingserviceapi.entity.TransactionHistory;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountInformationRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.service.SavingsAccountService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.TransactionType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Service
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final AccountInformationRepository accountInformationRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public SavingsAccountServiceImpl(AccountInformationRepository accountInformationRepository,
                                     TransactionHistoryRepository transactionHistoryRepository) {
        this.accountInformationRepository = accountInformationRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    @Override
    public AccountResponse openSavingsAccount(SavingsAccountRequest request) {
        AccountInformation account = accountInformationRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (!Objects.isNull(account)) {
            throw new BankingServiceException.AccountExistsException("Account already exists for given account number");
        }

        account = accountInformationRepository.save(new AccountInformation(request));

        return new AccountResponse(account.getAccountHolderName(), account.getAccountHolderSurname(), account.getAccountNumber(),
                account.getAccountType(), account.getAccountBalance(), account.getOverdraftBalance());
    }

    @Override
    public TransactionResponse withdrawalFromSavingsAccount(AccountWithdrawalRequest request) {
        AccountInformation account = accountInformationRepository.getAccountInformationByAccountNumber(
                request.accountNumber());

        if (Objects.isNull(account)) {
            throw new BankingServiceException.AccountNotFoundException("Withdrawal Error: Account doesn't exist");
        }

        if (account.getAccountBalance() <= 1000) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: Account doesn't have enough funds");
        }

        Double previousAccountBalance = account.getAccountBalance();
        Double newAccountBalance = BigDecimal.valueOf(previousAccountBalance)
                .subtract(BigDecimal.valueOf(request.withdrawalAmount())).doubleValue();

        if (newAccountBalance < 1000) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: Savings account doesnt have enough funds");
        }

        if (account.getAccountBalance() < request.withdrawalAmount()) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: Withdrawal amount exceeds the available balance");
        }

        if (!AccountType.SAVINGS.name().equals(account.getAccountType())) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: The account is not a savings account");
        }

        account.setAccountBalance(newAccountBalance);
        account.setUpdatedAt(new Date());

        accountInformationRepository.save(account);
        transactionHistoryRepository.save(new TransactionHistory(AccountType.SAVINGS.name(), account.getAccountNumber(),
                previousAccountBalance, newAccountBalance, TransactionType.WITHDRAWAL.name()));

        return new TransactionResponse(account.getAccountNumber(), AccountType.SAVINGS, previousAccountBalance,
                newAccountBalance, null);
    }

    @Override
    public TransactionResponse depositToSavingsAccount(AccountDepositRequest request) {
        AccountInformation account = accountInformationRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (Objects.isNull(account)) {
            throw new BankingServiceException.AccountNotFoundException("Withdrawal Error: Account doesn't exist");
        }

        if (!AccountType.SAVINGS.name().equals(account.getAccountType())) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: The account is not a savings account");
        }

        Double previousAccountBalance = account.getAccountBalance();
        Double newAccountBalance = BigDecimal.valueOf(previousAccountBalance)
                .add(BigDecimal.valueOf(request.depositAmount())).doubleValue();

        account.setAccountBalance(newAccountBalance);
        account.setUpdatedAt(new Date());

        accountInformationRepository.save(account);
        transactionHistoryRepository.save(new TransactionHistory(AccountType.SAVINGS.name(), account.getAccountNumber(),
                previousAccountBalance, newAccountBalance, TransactionType.DEPOSIT.name()));

        return new TransactionResponse(account.getAccountNumber(), AccountType.SAVINGS, previousAccountBalance,
                newAccountBalance, null);
    }
}
