package za.co.reed.shaun.bankingserviceapi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import za.co.reed.shaun.bankingserviceapi.entity.AccountInformation;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.repository.AccountInformationRepository;
import za.co.reed.shaun.bankingserviceapi.repository.TransactionHistoryRepository;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.exceptions.BankingServiceException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class SavingsAccountServiceImplTest {
    @Mock
    private AccountInformationRepository testAccountInformationRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    private SavingsAccountServiceImpl testSavingsAccountService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        testSavingsAccountService = new SavingsAccountServiceImpl(testAccountInformationRepository,
                transactionHistoryRepository);
    }


    /*
    * Opening an account tests -------
    * */

    @Test
    void openSavingsAccountWithSuccessfulFailureDueToAccountAlreadyExisting() {
        //Given
        SavingsAccountRequest testRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 0.0);
        AccountInformation testCurrentAccount = new AccountInformation(testRequest);


        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(testRequest.accountNumber()))
                .thenReturn(testCurrentAccount);

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
        AccountInformation testCurrentAccount = new AccountInformation(testRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        when(testAccountInformationRepository.save(any(AccountInformation.class)))
                .thenReturn(testCurrentAccount);

        //Then
        AccountResponse testAccountInformationResponse = testSavingsAccountService.openSavingsAccount(testRequest);

        assertNotNull(testAccountInformationResponse);
    }

    /*
     * Withdrawing from an account tests -------
     * */

    @Test
    void withdrawalFromSavingsAccountWithSuccessfulFailureDueToAccountNotFound() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        //Then
        assertThrows(BankingServiceException.AccountNotFoundException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void withdrawalFromSavingsAccountWithSuccessfulFailureDueToIncorrectAccountType() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        AccountInformation testAccountInformation = new AccountInformation(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 3000.0));

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccountInformation);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void withdrawalFromSavingsAccountWithSuccessfulFailureDueToAccountBalanceTooLow() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 1000.00);

        AccountInformation testAccountInformation = new AccountInformation(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 1000.0));

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccountInformation);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void withdrawalFromSavingsAccountWithSuccessfulFailureDueToAccountBalanceExceedingTheLimit() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        AccountInformation testAccountInformation = new AccountInformation(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 2000.0));

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccountInformation);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void withdrawalFromSavingsAccountWithSuccessfulFailureDueToWithdrawalAmountHigherThanTheAccountBalance() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 3000.00);

        AccountInformation testAccountInformation = new AccountInformation(new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 2000.0));

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testAccountInformation);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromSavingsAccountWithSuccessfulResponse() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 4000.00);
        SavingsAccountRequest testCurrentAccountRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 5000.00);
        AccountInformation testCurrentAccount = new AccountInformation(testCurrentAccountRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountInformationRepository.save(any(AccountInformation.class)))
                .thenReturn(testCurrentAccount);

        //Then
        TransactionResponse testTransactionResponse = testSavingsAccountService.withdrawalFromSavingsAccount(testRequest);

        assertEquals(1000.00, testTransactionResponse.accountBalance());
    }

    @Test
    void depositIntoSavingsAccountWithSuccessfulFailureDueToExceptionBecauseOfNoAccountInformation() {
        //Given
        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        //Then
        assertThrows(BankingServiceException.AccountNotFoundException.class, () -> {
            testSavingsAccountService.depositToSavingsAccount(testAccountDepositRequest);
        });
    }

    @Test
    void depositIntoSavingsAccountWithSuccessfulDepositIntoCurrentAccount() {
        //Given
        Double expectedResult = 12000.00;

        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);
        SavingsAccountRequest testCurrentAccountRequest = new SavingsAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 5000.00);
        AccountInformation testCurrentAccount = new AccountInformation(testCurrentAccountRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        TransactionResponse testTransactionResponse = testSavingsAccountService.depositToSavingsAccount(testAccountDepositRequest);

        //Then
        assertEquals(expectedResult, testTransactionResponse.accountBalance());
    }
}
