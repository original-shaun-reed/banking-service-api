package za.co.reed.shaun.bankingserviceapi.service.impl;

import org.springframework.stereotype.Service;
import za.co.reed.shaun.bankingserviceapi.entity.AccountInformation;
import za.co.reed.shaun.bankingserviceapi.entity.TransactionHistory;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountInformationRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.service.CurrentAccountService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.BankServiceConstants;
import za.co.reed.shaun.bankingserviceapi.utils.TransactionType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Service
public class CurrentAccountServiceImpl implements CurrentAccountService {
    private final AccountInformationRepository accountInformationRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public CurrentAccountServiceImpl(AccountInformationRepository accountInformationRepository,
                                     TransactionHistoryRepository transactionHistoryRepository) {
        this.accountInformationRepository = accountInformationRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    @Override
    public AccountResponse openCurrentAccount(CurrentAccountRequest request) {
        AccountInformation account = accountInformationRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (!Objects.isNull(account)) {
            throw new BankingServiceException.AccountExistsException("Account already exists for given account number");
        }

        account = accountInformationRepository.save(new AccountInformation(request));

        return new AccountResponse(account.getAccountHolderName(), account.getAccountHolderSurname(), account.getAccountNumber(),
                account.getAccountType(), account.getAccountBalance(), account.getOverdraftBalance());
    }

    @Override
    public TransactionResponse withdrawalFromCurrentAccount(AccountWithdrawalRequest request) {
        AccountInformation account = accountInformationRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (Objects.isNull(account)) {
            throw new BankingServiceException.AccountNotFoundException("Withdrawal Error: Account doesn't exist");
        }

        if (!AccountType.CURRENT.name().equals(account.getAccountType())) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: The account is not a current account");
        }

        Double previousAccountBalance = account.getAccountBalance();

        if (previousAccountBalance > 0) {
            Double newAccountBalance = BigDecimal.valueOf(previousAccountBalance)
                    .subtract(BigDecimal.valueOf(request.withdrawalAmount())).doubleValue();

            if (newAccountBalance < 0) {
                BigDecimal.valueOf(account.getOverdraftBalance()).subtract(BigDecimal.valueOf(newAccountBalance)).doubleValue();

                account = updateAccountInformationForOverdraft(account, newAccountBalance);
            } else {
                account.setAccountBalance(newAccountBalance);
                account.setUpdatedAt(new Date());
            }
        } else {
            Double overdraft = BigDecimal.valueOf(account.getOverdraftBalance())
                    .subtract(BigDecimal.valueOf(request.withdrawalAmount())).doubleValue();
            account = updateAccountInformationForOverdraft(account, overdraft);
        }

        accountInformationRepository.save(account);
        transactionHistoryRepository.save(new TransactionHistory(AccountType.CURRENT.name(), account.getAccountNumber(),
                previousAccountBalance, account.getAccountBalance(), TransactionType.WITHDRAWAL.name()));

        return new TransactionResponse(account.getAccountNumber(), AccountType.CURRENT, previousAccountBalance,
                account.getAccountBalance(), account.getOverdraftBalance());
    }

    @Override
    public TransactionResponse depositToCurrentAccount(AccountDepositRequest request) {
        AccountInformation account = accountInformationRepository.getAccountInformationByAccountNumber(request.accountNumber());

        if (Objects.isNull(account)) {
            throw new BankingServiceException.AccountNotFoundException("Withdrawal Error: Account doesn't exist");
        }

        if (!AccountType.CURRENT.name().equals(account.getAccountType())) {
            throw new BankingServiceException.AccountTransactionException("Withdrawal Error: The account is not a current account");
        }

        Double previousAccountBalance = account.getAccountBalance();
        Double newAccountBalance = BigDecimal.valueOf(previousAccountBalance)
                .add(BigDecimal.valueOf(request.depositAmount())).doubleValue();

        account.setAccountBalance(newAccountBalance);
        account.setUpdatedAt(new Date());

        accountInformationRepository.save(account);
        transactionHistoryRepository.save(new TransactionHistory(AccountType.CURRENT.name(), account.getAccountNumber(),
                previousAccountBalance, newAccountBalance, TransactionType.DEPOSIT.name()));

        return new TransactionResponse(account.getAccountNumber(), AccountType.CURRENT, previousAccountBalance,
                newAccountBalance, account.getOverdraftBalance());
    }

    private AccountInformation updateAccountInformationForOverdraft(AccountInformation account, Double overdraft) {
        if (overdraft >= BankServiceConstants.overdraftAmount) {
            account.setAccountBalance(BigDecimal.ZERO.doubleValue());
            account.setOverdraftBalance(overdraft);
            account.setUpdatedAt(new Date());

            return account;
        }

        throw new BankingServiceException.AccountTransactionException("Withdrawal error: No funds available in account");
    }
}
