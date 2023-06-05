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
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
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
class CurrentAccountServiceImplTest {
    @Mock
    private AccountInformationRepository testAccountInformationRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    private CurrentAccountServiceImpl testCurrentAccountService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        testCurrentAccountService = new CurrentAccountServiceImpl(testAccountInformationRepository,
                transactionHistoryRepository);
    }

    @Test
    void openCurrentAccountWithSuccessfulFailureDueToAccountAlreadyExisting() {
        //Given
        CurrentAccountRequest testRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0);
        AccountInformation testCurrentAccount = new AccountInformation(testRequest);


        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(testRequest.accountNumber()))
                .thenReturn(testCurrentAccount);

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
        AccountInformation testCurrentAccount = new AccountInformation(testRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        when(testAccountInformationRepository.save(any(AccountInformation.class)))
                .thenReturn(testCurrentAccount);

        //Then
        AccountInformation testAccountInformationResponse = testCurrentAccountService
                .openCurrentAccount(testRequest);

        assertNotNull(testAccountInformationResponse);
    }

    @Test
    void withdrawalFromCurrentAccountWithSuccessfulFailureDueToAccountNotFound() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 2000.00);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
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

        AccountInformation testAccountInformation = new AccountInformation(new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 0.0));

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
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
        AccountInformation testCurrentAccount = new AccountInformation(testCurrentAccountRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountInformationRepository.save(any(AccountInformation.class)))
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
        AccountInformation testCurrentAccount = new AccountInformation(testCurrentAccountRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountInformationRepository.save(any(AccountInformation.class)))
                .thenReturn(testCurrentAccount);

        //Then
        TransactionResponse testTransactionResponse = testCurrentAccountService
                .withdrawalFromCurrentAccount(testRequest);

        assertTrue((BigDecimal.ZERO.doubleValue() > testTransactionResponse.overDraftBalance()));
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulFailureResponseWithOverdraftTooHigh() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 105000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.00);
        AccountInformation testCurrentAccount = new AccountInformation(testCurrentAccountRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        //Then
        assertThrows(BankingServiceException.AccountTransactionException.class, () -> {
            testCurrentAccountService.withdrawalFromCurrentAccount(testRequest);
        });
    }

    @Test
    void testWithdrawalFromCurrentAccountWithSuccessfulResponseWithOverdraftResponseWithNoAccountBalance() {
        //Given
        AccountWithdrawalRequest testRequest = new AccountWithdrawalRequest(1234567890, 7000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 0.0);
        AccountInformation testCurrentAccount = new AccountInformation(testCurrentAccountRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        when(testAccountInformationRepository.save(any(AccountInformation.class)))
                .thenReturn(testCurrentAccount);

        //Then
        TransactionResponse testTransactionResponse = testCurrentAccountService
                .withdrawalFromCurrentAccount(testRequest);

        assertTrue((BigDecimal.ZERO.doubleValue() > testTransactionResponse.overDraftBalance()));
    }

    @Test
    void depositIntoCurrentAccountWithSuccessfulFailureDueToExceptionAndIncorrectAccountType() {
        //Given
        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.SAVINGS, 5000.00);
        AccountInformation testCurrentAccount = new AccountInformation(testCurrentAccountRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
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
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(null);

        //Then
        assertThrows(BankingServiceException.AccountNotFoundException.class, () -> {
            testCurrentAccountService.depositToCurrentAccount(testAccountDepositRequest);
        });
    }

    @Test
    void depositIntoCurrentAccountWithSuccessfulDepositIntoCurrentAccount() {
        //Given
        Double expectedResult = 12000.00;

        AccountDepositRequest testAccountDepositRequest = new AccountDepositRequest(1234567890, 7000.00);
        CurrentAccountRequest testCurrentAccountRequest = new CurrentAccountRequest("TEST",
                "TEST", 1234567890, AccountType.CURRENT, 5000.00);
        AccountInformation testCurrentAccount = new AccountInformation(testCurrentAccountRequest);

        //When
        when(testAccountInformationRepository.getAccountInformationByAccountNumber(anyInt()))
                .thenReturn(testCurrentAccount);

        TransactionResponse testTransactionResponse = testCurrentAccountService.depositToCurrentAccount(testAccountDepositRequest);

        //Then
        assertEquals(expectedResult, testTransactionResponse.accountBalance());
    }

    @Test
    void depositToCurrentAccount() {
    }
}
