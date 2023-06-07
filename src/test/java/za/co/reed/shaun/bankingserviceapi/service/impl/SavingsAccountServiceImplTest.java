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
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.service.AccountService;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class SavingsAccountServiceImplTest {
    @Mock
    private AccountRepository testAccountRepository;
    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;
    @Mock
    private AccountService testAccountService;

    @InjectMocks
    private SavingsAccountServiceImpl testSavingsAccountService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        testSavingsAccountService = new SavingsAccountServiceImpl(testAccountRepository,
                transactionHistoryRepository, testAccountService);
    }


    /*
    * Opening an account tests -------
    * */

    @Test
    void testOpenSavingsAccountWithSuccessfulFailureDueToAccountAlreadyExisting() {
        //Given
        SavingsAccountRequest testRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 0.0);
        Account testCurrentAccount = new Account(testRequest);


        //When
        when(testAccountService.openSavingsAccount(any(SavingsAccountRequest.class)))
                .thenThrow(BankingServiceException.AccountExistsException.class);

        //Then
        assertThrows(BankingServiceException.AccountExistsException.class, () -> {
            testSavingsAccountService.openSavingsAccount(testRequest);
        });
    }

    @Test
    void testCreationOfSavingsAccountWithSuccessfulSaveToTheDB() {
        //Given
        SavingsAccountRequest testRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 0.0);
        Account testCurrentAccount = new Account(testRequest);

        //When
        when(testAccountService.openSavingsAccount(any(SavingsAccountRequest.class)))
                .thenReturn(testCurrentAccount);

        //Then
        AccountResponse testAccountInformationResponse = testSavingsAccountService.openSavingsAccount(testRequest);

        assertNotNull(testAccountInformationResponse);
    }

    /*
     * Withdrawing from an account tests -------
     * */

    @Test
    void testWithdrawalFromSavingsAccountWithSuccessfulFailureDueToAccountNotFound() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        //Then
        assertThrows(BankingServiceException.AccountNotFoundException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromSavingsAccountWithSuccessfulFailureDueToIncorrectAccountType() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        Account testAccountInformation = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 3000.0));

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccountInformation);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromSavingsAccountWithSuccessfulFailureDueToAccountBalanceTooLow() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 1000.00);

        Account testAccount = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 1000.0));

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccount);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromSavingsAccountWithSuccessfulFailureDueToAccountBalanceExceedingTheLimit() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        Account testAccount = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 2000.0));

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccount);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromSavingsAccountWithSuccessfulFailureDueToWithdrawalAmountHigherThanTheAccountBalance() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 3000.00);

        Account testAccount = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 2000.0));

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccount);

        when(testAccountService.withdrawFromSavingsAccount(any(Account.class), anyDouble()))
                .thenThrow(BankingServiceException.AccountTransactionException.class);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromSavingsAccountWithSuccessfulResponse() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 4000.00);
        SavingsAccountRequest testAccountRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 5000.00);
        Account testSavingsAccount = new Account(testAccountRequest);

        Account expected = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS,
                testSavingsAccount.getAccountBalance() - testRequest.withdrawalAmount()));

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testSavingsAccount);

        when(testAccountService.withdrawFromSavingsAccount(any(Account.class), anyDouble()))
                .thenReturn(expected);

        //Then
        TransactionResponse testTransactionResponse = testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);

        assertEquals(1000.00, testTransactionResponse.accountBalance());
    }

    @Test
    void testDepositIntoSavingsAccountWithSuccessfulFailureDueToExceptionBecauseOfNoAccountInformation() {
        //Given
        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        //Then
        assertThrows(BankingServiceException.AccountNotFoundException.class, () -> {
            testSavingsAccountService.depositToSavingsAccount(testAccountDepositRequest);
        });
    }

    @Test
    void testDepositIntoSavingsAccountWithSuccessfulDepositIntoCurrentAccount() {
        //Given
        Double expectedResult = 12000.00;

        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);
        SavingsAccountRequest testAccountRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 5000.00);
        Account testAccount = new Account(testAccountRequest);

        Account expected = testAccount;
        expected.setAccountBalance(testAccountDepositRequest.depositAmount() + expected.getAccountBalance());


        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccount);
        when(testAccountService.depositIntoAccount(any(Account.class), anyDouble()))
                .thenReturn(expected);

        TransactionResponse testTransactionResponse = testSavingsAccountService.depositToSavingsAccount(testAccountDepositRequest);

        //Then
        assertEquals(expectedResult, testTransactionResponse.accountBalance());
    }
}
