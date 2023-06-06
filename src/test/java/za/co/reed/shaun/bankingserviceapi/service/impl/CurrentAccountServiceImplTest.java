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
import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.service.AccountService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class CurrentAccountServiceImplTest {
    @Mock
    private AccountRepository testAccountRepository;
    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;
    @Mock
    private AccountService testAccountService;

    @InjectMocks
    private CurrentAccountServiceImpl testCurrentAccountService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        testCurrentAccountService = new CurrentAccountServiceImpl(testAccountRepository,
                transactionHistoryRepository, testAccountService);
    }

    @Test
    void openCurrentAccountWithSuccessfulFailureDueToAccountAlreadyExisting() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0);
        Account testCurrentAccount = new Account(testRequest);


        //When
        when(testAccountService.openCurrentAccount(any(CurrentAccountRequest.class)))
                .thenThrow(BankingServiceException.AccountExistsException.class);

        //Then
        assertThrows(BankingServiceException.AccountExistsException.class, () -> {
            testCurrentAccountService.openCurrentAccount(testRequest);
        });
    }

    @Test
    void testCreationOfCurrentAccountWithSuccessfulSaveToTheDB() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0);
        Account testCurrentAccount = new Account(testRequest);

        //When
        when(testAccountService.openCurrentAccount(any(CurrentAccountRequest.class)))
                .thenReturn(testCurrentAccount);

        //Then
        AccountResponse testAccountInformationResponse = testCurrentAccountService
                .openCurrentAccount(testRequest);

        assertNotNull(testAccountInformationResponse);
    }

    @Test
    void withdrawalFromCurrentAccountWithSuccessfulFailureDueToAccountNotFound() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        //Then
        assertThrows(BankingServiceException.AccountNotFoundException.class, () -> {
            testCurrentAccountService.withdrawalFromCurrentAccount(testRequest);
        });
    }

    @Test
    void withdrawalFromCurrentAccountWithSuccessfulFailureDueToIncorrectAccountType() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        Account testAccountInformation = new Account(new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 0.0));

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccountInformation);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testCurrentAccountService.withdrawalFromCurrentAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulResponse() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 5000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 5000.00);
        Account testCurrentAccount = new Account(testCurrentAccountRequest);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountService.withdrawFromCurrentAccount(any(Account.class), anyDouble()))
                .thenReturn(testCurrentAccount);

        //Then
        TransactionResponse testTransactionResponse = testCurrentAccountService
                .withdrawalFromCurrentAccount(testRequest);

        assertNotNull(testTransactionResponse);
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulResponseWithOverdraftResponse() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 7000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 5000.00);
        Account testCurrentAccount = new Account(testCurrentAccountRequest);

        Account expectedCurrentAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0));
        expectedCurrentAccount.setOverdraftBalance(-2000.00);


        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountService.withdrawFromCurrentAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedCurrentAccount);

        //Then
        TransactionResponse testTransactionResponse = testCurrentAccountService
                .withdrawalFromCurrentAccount(testRequest);

        assertTrue((BigDecimal.ZERO.doubleValue() > testTransactionResponse.overdraftBalance()));
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulFailureResponseWithOverdraftTooHigh() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 105000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.00);
        Account testCurrentAccount = new Account(testCurrentAccountRequest);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt())).thenReturn(testCurrentAccount);

        when(testAccountService.withdrawFromCurrentAccount(any(Account.class), anyDouble()))
                .thenThrow(BankingServiceException.AccountTransactionException.class);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testCurrentAccountService.withdrawalFromCurrentAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulResponseWithOverdraftResponseWithNoAccountBalance() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 7000.00);

        Account testCurrentAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0));


        Account expectedCurrentAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0));
        expectedCurrentAccount.setOverdraftBalance(-5000.00);


        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountService.withdrawFromCurrentAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedCurrentAccount);

        //Then
        TransactionResponse testTransactionResponse = testCurrentAccountService
                .withdrawalFromCurrentAccount(testRequest);

        assertTrue((BigDecimal.ZERO.doubleValue() > testTransactionResponse.overdraftBalance()));
    }

    @Test
    void depositIntoCurrentAccountWithSuccessfulFailureDueToExceptionAndIncorrectAccountType() {
        //Given
        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 5000.00);
        Account testCurrentAccount = new Account(testCurrentAccountRequest);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testCurrentAccountService.depositToCurrentAccount(testAccountDepositRequest);
        });
    }

    @Test
    void depositIntoCurrentAccountWithSuccessfulFailureDueToException() {
        //Given
        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        //Then
        assertThrows(BankingServiceException.AccountNotFoundException.class, () -> {
            testCurrentAccountService.depositToCurrentAccount(testAccountDepositRequest);
        });
    }

    @Test
    void depositIntoCurrentAccountWithSuccessfulDepositIntoCurrentAccount() {
        //Given
        Double expectedResult = 2000.00;

        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);

        Account testCurrentAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0));
        testCurrentAccount.setOverdraftBalance(-5000.00);

        Account expectedCurrentAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 2000.00));

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountService.depositIntoAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedCurrentAccount);

        TransactionResponse testTransactionResponse = testCurrentAccountService.depositToCurrentAccount(testAccountDepositRequest);

        //Then
        assertEquals(expectedResult, testTransactionResponse.accountBalance());
    }

    @Test
    void depositIntoCurrentAccountWithSuccessfulDepositIntoCurrentAccountOverdraft() {
        //Given
        Double expectedResult = -2000.00;

        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0);
        Account testCurrentAccount = new Account(testCurrentAccountRequest);
        testCurrentAccount.setOverdraftBalance(-9000.00);

        Account expectedCurrentAccount = new Account(new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0));
        expectedCurrentAccount.setOverdraftBalance(-2000.00);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountService.depositIntoAccount(any(Account.class), anyDouble()))
                .thenReturn(expectedCurrentAccount);

        TransactionResponse testTransactionResponse = testCurrentAccountService.depositToCurrentAccount(testAccountDepositRequest);

        //Then
        assertEquals(expectedResult, testTransactionResponse.overdraftBalance());
    }
}
