package za.co.reed.shaun.bankingserviceapi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import za.co.reed.shaun.bankingserviceapi.entity.Account;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.TransferRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.TransferResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.service.AccountService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class TransferServiceImplTest {
    @Mock
    private AccountRepository testAccountRepository;
    @Mock
    private TransactionHistoryRepository testTransactionHistoryRepository;
    @Mock
    private AccountService testAccountService;

    @InjectMocks
    private TransferServiceImpl testTransferService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        testTransferService = new TransferServiceImpl(testAccountRepository,
                testTransactionHistoryRepository, testAccountService);
    }

    @Test
    void testTransferFromCurrentAccountToCurrentAccountWithSuccessfulResponse() {
        //Given
        TransferRequest testTransferRequest = new TransferRequest(12345678, 123456789, 500.00);

        Account testFromAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 12345678, AccountType.CURRENT, 1000.0));

        Account testToAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 123456789, AccountType.CURRENT, 2000.0));

        Account expectedFromAccount = testFromAccount;
        expectedFromAccount.setAccountBalance(expectedFromAccount.getAccountBalance() - testTransferRequest.transferAmount());

        Account expectedToAccount = testToAccount;
        expectedToAccount.setAccountBalance(expectedToAccount.getAccountBalance() + testTransferRequest.transferAmount());

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.fromAccountNumber()))
                .thenReturn(testFromAccount);

        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.toAccountNumber()))
                .thenReturn(testToAccount);

        when(testAccountService.withdrawFromCurrentAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedFromAccount);

        when(testAccountService.depositIntoAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedToAccount);

        //Then
        TransferResponse testTransferResponse = testTransferService.transferMoney(testTransferRequest);

        assertEquals(2500.00, testTransferResponse.updatedToAmount());
    }


    @Test
    void testTransferFromCurrentAccountToCurrentAccountWithSuccessfulResponseFromOverdraftAccount() {
        //Given
        TransferRequest testTransferRequest = new TransferRequest(12345678, 123456789, 1200.00);

        Account testFromAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 12345678, AccountType.CURRENT, 1000.0));

        Account testToAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 123456789, AccountType.CURRENT, 100.00));
        testToAccount.setOverdraftBalance(-1000.00);

        Account expectedFromAccount = testFromAccount;
        expectedFromAccount.setAccountBalance(expectedFromAccount.getAccountBalance() - testTransferRequest.transferAmount());

        Account expectedToAccount = testToAccount;
        expectedToAccount.setAccountBalance(expectedToAccount.getOverdraftBalance() + testTransferRequest.transferAmount());


        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.fromAccountNumber()))
                .thenReturn(testFromAccount);

        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.toAccountNumber()))
                .thenReturn(testToAccount);

        when(testAccountService.withdrawFromCurrentAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedFromAccount);

        when(testAccountService.depositIntoAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedToAccount);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.fromAccountNumber()))
                .thenReturn(testFromAccount);

        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.toAccountNumber()))
                .thenReturn(testToAccount);

        //Then
        TransferResponse testTransferResponse = testTransferService.transferMoney(testTransferRequest);

        assertEquals(200.00, testTransferResponse.updatedToAmount());
    }

    /*
    * SAVINGS ACCOUNT TESTS
    * */

    @Test
    void testTransferFromSavingsAccountToSavingsAccountWithSuccessfulResponse() {
        //Given
        TransferRequest testTransferRequest = new TransferRequest(12345678, 123456789, 1000.00);

        Account testFromAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 12345678, AccountType.SAVINGS, 2000.00));

        Account testToAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 123456789, AccountType.SAVINGS, 2000.00));

        Account expectedFromAccount = testFromAccount;
        expectedFromAccount.setAccountBalance(expectedFromAccount.getAccountBalance() - testTransferRequest.transferAmount());

        Account expectedToAccount = testToAccount;
        expectedToAccount.setAccountBalance(expectedToAccount.getAccountBalance() + testTransferRequest.transferAmount());

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.fromAccountNumber()))
                .thenReturn(testFromAccount);

        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.toAccountNumber()))
                .thenReturn(testToAccount);

        when(testAccountService.withdrawFromSavingsAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedFromAccount);

        when(testAccountService.depositIntoAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedToAccount);

        //Then
        TransferResponse testTransferResponse = testTransferService.transferMoney(testTransferRequest);

        assertEquals(3000.00, testTransferResponse.updatedToAmount());
    }

    @Test
    void testTransferFromSavingsAccountToCurrentAccountWithSuccessfulResponse() {
        //Given
        TransferRequest testTransferRequest = new TransferRequest(12345678, 123456789, 1000.00);

        Account testFromAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 12345678, AccountType.SAVINGS, 2000.0));

        Account testToAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 123456789, AccountType.CURRENT, 2000.0));

        Account expectedFromAccount = testFromAccount;
        expectedFromAccount.setAccountBalance(expectedFromAccount.getAccountBalance() - testTransferRequest.transferAmount());

        Account expectedToAccount = testToAccount;
        expectedToAccount.setAccountBalance(expectedToAccount.getAccountBalance() + testTransferRequest.transferAmount());

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.fromAccountNumber()))
                .thenReturn(testFromAccount);

        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.toAccountNumber()))
                .thenReturn(testToAccount);

        when(testAccountService.withdrawFromSavingsAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedFromAccount);

        when(testAccountService.depositIntoAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedToAccount);

        //Then
        TransferResponse testTransferResponse = testTransferService.transferMoney(testTransferRequest);

        assertEquals(3000.00, testTransferResponse.updatedToAmount());
    }

    @Test
    void testTransferFromSavingsAccountToCurrentAccountWithSuccessfulResponseOnOverdraftBalance() {
        //Given
        TransferRequest testTransferRequest = new TransferRequest(12345678, 123456789, 1000.00);

        Account testFromAccount = new Account(new SavingsAccountRequest("TEST",
                "TEST", 12345678, AccountType.SAVINGS, 2000.0));

        Account testToAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 123456789, AccountType.CURRENT, 00.0));
        testToAccount.setOverdraftBalance(-5000.00);


        Account expectedFromAccount = testFromAccount;
        expectedFromAccount.setAccountBalance(expectedFromAccount.getAccountBalance() - testTransferRequest.transferAmount());

        Account expectedToAccount = testToAccount;
        expectedToAccount.setOverdraftBalance(testToAccount.getOverdraftBalance() + testTransferRequest.transferAmount());

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.fromAccountNumber()))
                .thenReturn(testFromAccount);

        when(testAccountRepository.getAccountInformationByAccountNumber(testTransferRequest.toAccountNumber()))
                .thenReturn(testToAccount);

        when(testAccountService.withdrawFromSavingsAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedFromAccount);

        when(testAccountService.depositIntoAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedToAccount);

        //Then
        TransferResponse testTransferResponse = testTransferService.transferMoney(testTransferRequest);

        assertEquals(-4000.00, testTransferResponse.updatedOverdraftAmount());
    }
}
