package za.co.reed.shaun.bankingserviceapi.model.request;

import jakarta.validation.constraints.*;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.BankServiceConstants;

import java.io.Serializable;

public record CurrentAccountRequest(
    @NotNull(message = BankServiceConstants.validateAccountHolderNameMsg)
    @NotEmpty(message = BankServiceConstants.validateAccountHolderNameMsg)
    @NotBlank(message = BankServiceConstants.validateAccountHolderNameMsg)
    String accountHolderName,

    @NotNull(message = BankServiceConstants.validateAccountHolderNameMsg)
    @NotEmpty(message = BankServiceConstants.validateAccountHolderNameMsg)
    @NotBlank(message = BankServiceConstants.validateAccountHolderNameMsg)
    String accountHolderSurname,

    @NotNull(message = BankServiceConstants.validateAccountNumberMsg)
    @Positive(message = BankServiceConstants.validateAccountNumberNegativeMsg)
    Integer accountNumber,

    @NotNull(message = BankServiceConstants.validateAccountTypeMsg)
    AccountType accountType,

    @Positive(message = BankServiceConstants.validateAmountNegativeMsg)
    Double amountToDeposit
) implements Serializable {}
