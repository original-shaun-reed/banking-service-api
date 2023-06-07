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
import za.co.reed.shaun.bankingserviceapi.repository.AccountRepository;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class AccountServiceImplTest {
    @Mock
    private AccountRepository testAccountRepository;

    @InjectMocks
    private AccountServiceImpl testAccountService;

    @BeforeEach
    void setUp() {
        this.testAccountService = new AccountServiceImpl(testAccountRepository);
    }

    @Test
    void openCurrentAccountWithSuccessfulSaveToDB() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0);
        Account testAccount = new Account(testRequest);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        when(testAccountRepository.save(any(Account.class)))
                .thenReturn(testAccount);

        //Then
        Account testAccountResponse = testAccountService.openCurrentAccount(testRequest);

        assertNotNull(testAccountResponse);
    }

    @Test
    void openCurrentAccountWithSuccessfulFailureDueToAccountAlreadyExists() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0);
        Account testAccount = new Account(testRequest);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccount);

        //Then
        assertThrows(BankingServiceException.AccountExistsException.class,
                () -> testAccountService.openCurrentAccount(testRequest));
    }

    @Test
    void testOpenCurrentAccountWithSuccessfulFailureDueToAccountTypeBeingIncorrectOrNull() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, null, null);


        //Then
        assertThrows(BankingServiceException.AccountTypeNotFoundException.class,
                () -> testAccountService.openCurrentAccount(testRequest));
    }

    @Test
    void testOpenSavingsAccountWithSuccessfulSaveToDB() {
        //Given
        SavingsAccountRequest testRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0);
        Account testAccount = new Account(testRequest);

        //When
        when(testAccountRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        when(testAccountRepository.save(any(Account.class)))
                .thenReturn(testAccount);

        //Then
        Account testAccountResponse = testAccountService.openSavingsAccount(testRequest);

        assertNotNull(testAccountResponse);
    }

    @Test
    void testDepositIntoCurrentAccountWithSuccessfulAmendmentToAccountBalance() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 1000.00);
        Account testAccount = new Account(testRequest);
        testAccount.setOverdraftBalance(0.0);

        //When
        Account testResponseAccount = testAccountService.depositIntoAccount(testAccount, 5000.00);

        //Then
        assertEquals(6000.00, testResponseAccount.getAccountBalance());
    }

    @Test
    void testDepositIntoSavingsAccountWithSuccessfulAmendmentToAccountBalance() {
        //Given
        SavingsAccountRequest testRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 1000.00);
        Account testAccount = new Account(testRequest);

        //When
        Account testResponseAccount = testAccountService.depositIntoAccount(testAccount, 7000.00);

        //Then
        assertEquals(8000.00, testResponseAccount.getAccountBalance());
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulAmendmentToAccountBalance() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 5000.00);
        Account testAccount = new Account(testRequest);
        testAccount.setOverdraftBalance(0.0);

        //When
        Account testResponseAccount = testAccountService.withdrawFromCurrentAccount(testAccount, 4000.00);

        //Then
        assertEquals(1000.00, testResponseAccount.getAccountBalance());
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulAmendmentToAccountBalanceAndOverdraftBalance() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 5000.00);
        Account testAccount = new Account(testRequest);
        testAccount.setOverdraftBalance(0.0);

        //When
        Account testResponseAccount = testAccountService.withdrawFromCurrentAccount(testAccount, 6000.00);

        //Then
        assertEquals(BigDecimal.ZERO.doubleValue(), testResponseAccount.getAccountBalance());
        assertEquals(-1000, testResponseAccount.getOverdraftBalance());
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulAddingToOverdraftBalance() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 5000.00);
        Account testAccount = new Account(testRequest);
        testAccount.setOverdraftBalance(00.0);

        //When
        Account testResponseAccount = testAccountService.withdrawFromCurrentAccount(testAccount, 6000.00);

        //Then
        assertEquals(BigDecimal.ZERO.doubleValue(), testResponseAccount.getAccountBalance());
        assertEquals(-1000, testResponseAccount.getOverdraftBalance());
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulAddingToOverdraftBalanceWhileItsZero() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 00.00);
        Account testAccount = new Account(testRequest);
        testAccount.setOverdraftBalance(00.0);

        //When
        Account testResponseAccount = testAccountService.withdrawFromCurrentAccount(testAccount, 6000.00);

        //Then
        assertEquals(BigDecimal.ZERO.doubleValue(), testResponseAccount.getAccountBalance());
        assertEquals(-6000, testResponseAccount.getOverdraftBalance());
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulFailureByExceedingOverdraftAmount() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 00.00);
        Account testAccount = new Account(testRequest);
        testAccount.setOverdraftBalance(-99000.0);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class,
                () -> testAccountService.withdrawFromCurrentAccount(testAccount, 6000.00));
    }

    @Test
    void testWithdrawalFromSavingsAccountSuccessfulFailureDueToAccountBalanceBeingLessThanWithdrawalAmount() {
       //Given
        Account testAccount = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 1000.00));

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class,
                () -> testAccountService.withdrawFromSavingsAccount(testAccount, 6000.00));
    }

    @Test
    void testWithdrawalFromSavingsAccountSuccessfulFailureDueToAccountBeingBelowTheMinimumAmount() {
        //Given
        Account testAccount = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 1000.00));

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class,
                () -> testAccountService.withdrawFromSavingsAccount(testAccount, 1000.00));
    }

    @Test
    void testWithdrawalFromSavingsAccountSuccessfulFailureDueToAccountBalanceWillBeBelowMinimumAfterWithdrawal() {
        //Given
        Account testAccount = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 9000.00));

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class,
                () -> testAccountService.withdrawFromSavingsAccount(testAccount, 8500.00));
    }

    @Test
    void testWithdrawalFromSavingsAccountSuccessfullyMeetingAllTheRequirements() {
        //Given
        Account testAccount = new Account(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 10000.00));

        //Given
        Account testAccountResponse = testAccountService.withdrawFromSavingsAccount(testAccount, 8000.00);


        //Then
        assertEquals(2000.00, testAccountResponse.getAccountBalance());
    }
}
