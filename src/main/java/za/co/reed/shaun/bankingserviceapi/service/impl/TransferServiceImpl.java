package za.co.reed.shaun.bankingserviceapi.service.impl;

import org.springframework.stereotype.Service;
import za.co.reed.shaun.bankingserviceapi.entity.Account;
import za.co.reed.shaun.bankingserviceapi.entity.TransactionHistory;
import za.co.reed.shaun.bankingserviceapi.model.request.TransferRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.TransferResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.service.AccountService;
import za.co.reed.shaun.bankingserviceapi.service.TransferService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.TransactionType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.util.Date;
import java.util.Objects;

@Service
public class TransferServiceImpl implements TransferService {
    private final AccountRepository accountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    private final AccountService accountService;

    public TransferServiceImpl(AccountRepository accountRepository,
                               TransactionHistoryRepository transactionHistoryRepository,
                               AccountService accountService) {
        this.accountRepository = accountRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.accountService = accountService;
    }

    @Override
    public TransferResponse transferMoney(TransferRequest transferRequest) {
        Account fromAccount = accountRepository.getAccountInformationByAccountNumber(transferRequest.fromAccountNumber());

        if (Objects.isNull(fromAccount)) {
            throw new BankingServiceException.AccountNotFoundException("Transfer Error: Account to transfer from doesn't exist");
        }

        Account toAccount = accountRepository.getAccountInformationByAccountNumber(transferRequest.toAccountNumber());

        if (Objects.isNull(toAccount)) {
            throw new BankingServiceException.AccountNotFoundException("Transfer Error: Account to transfer to doesn't exist");
        }

        return transferMoneyFromAccount(fromAccount, toAccount, transferRequest.transferAmount());
    }

    private TransferResponse transferMoneyFromAccount(Account fromAccount, Account toAccount,
                                                      Double transferAmount) {
        Double previousAccountBalance = fromAccount.getAccountBalance();
        Double previousOverdraftBalance = fromAccount.getOverdraftBalance();

        if (AccountType.CURRENT.name().equalsIgnoreCase(fromAccount.getAccountType())) {
            fromAccount = updateFromCurrentAccountForTransfer(fromAccount, transferAmount);
        } else if(AccountType.SAVINGS.name().equalsIgnoreCase(fromAccount.getAccountType())) {
            fromAccount = updateFromSavingsAccountForTransfer(fromAccount, transferAmount);
        } else {
            throw new BankingServiceException.AccountTypeNotFoundException("Transfer Error: Unable to determine account type");
        }

        toAccount = updateToAccountInformation(toAccount, transferAmount);
        toAccount.setUpdatedAt(new Date());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transactionHistoryRepository.save(new TransactionHistory(fromAccount.getAccountType(), fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(), previousAccountBalance, fromAccount.getAccountBalance(),
                previousOverdraftBalance, fromAccount.getOverdraftBalance(), TransactionType.TRANSFER.name()));


        String transactionResponseMessage = createTransactionResponseMessage(fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(), transferAmount);
        return new TransferResponse(transactionResponseMessage, toAccount.getAccountBalance(), toAccount.getOverdraftBalance());
    }

    // Updates the Current Account from which the money is transferred from
    private Account updateFromCurrentAccountForTransfer(Account fromAccount, Double transferAmount) {
        fromAccount = accountService.withdrawFromCurrentAccount(fromAccount, transferAmount);
        fromAccount.setUpdatedAt(new Date());
        return fromAccount;
    }

    private Account updateFromSavingsAccountForTransfer(Account fromAccount, Double transferAmount) {
        fromAccount = accountService.withdrawFromSavingsAccount(fromAccount, transferAmount);
        fromAccount.setUpdatedAt(new Date());
        return fromAccount;
    }

    // Updating the account information for account money is being transferred to
    private Account updateToAccountInformation(Account toAccount, Double transferAmount) {
        if (AccountType.CURRENT.name().equalsIgnoreCase(toAccount.getAccountType())) {
            return accountService.depositIntoAccount(toAccount, transferAmount);
        } else if(AccountType.SAVINGS.name().equalsIgnoreCase(toAccount.getAccountType())) {
            return accountService.depositIntoAccount(toAccount, transferAmount);
        }

        throw new BankingServiceException.AccountTypeNotFoundException("Transfer Error: Unable to determine account type");
    }

    private String createTransactionResponseMessage(Integer fromAccountNumber, Integer toAccountNumber, Double transferAmount) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Transfer from ");
        messageBuilder.append(fromAccountNumber);
        messageBuilder.append(" to ");
        messageBuilder.append(toAccountNumber);
        messageBuilder.append(" with the amount of R");
        messageBuilder.append(transferAmount);
        messageBuilder.append(" was successful");

        return messageBuilder.toString();
    }
}
