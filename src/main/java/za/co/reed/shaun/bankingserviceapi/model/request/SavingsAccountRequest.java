package za.co.reed.shaun.bankingserviceapi.model.request;

import jakarta.validation.constraints.*;
import za.co.reed.shaun.bankingserviceapi.utils.AccountType;
import za.co.reed.shaun.bankingserviceapi.utils.BankServiceConstants;

import java.io.Serializable;

public record SavingsAccountRequest (
  @NotNull(message = BankServiceConstants.validateAccountHolderNameMsg)
  @NotEmpty(message = BankServiceConstants.validateAccountHolderNameMsg)
  @NotBlank(message = BankServiceConstants.validateAccountHolderNameMsg)
  String accountHolderName,

  @NotNull(message = BankServiceConstants.validateAccountHolderSurnameMsg)
  @NotEmpty(message = BankServiceConstants.validateAccountHolderSurnameMsg)
  @NotBlank(message = BankServiceConstants.validateAccountHolderSurnameMsg)
  String accountHolderSurname,

  @NotNull(message = BankServiceConstants.validateAccountNumberMsg)
  @Positive(message = BankServiceConstants.validateAccountNumberNegativeMsg)
  Integer accountNumber,

  @NotNull(message = BankServiceConstants.validateAccountTypeMsg)
  AccountType accountType,

  @NotNull(message = BankServiceConstants.validateAmountMsg)
  @Positive(message = BankServiceConstants.validateAmountNegativeMsg)
  @Min(value = 1000, message = BankServiceConstants.validateMininumAmountMsg)
  Double amountToDeposit
) implements Serializable {}
