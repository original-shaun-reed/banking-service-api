package za.co.reed.shaun.bankingserviceapi.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import za.co.reed.shaun.bankingserviceapi.utils.BankServiceConstants;

public record AccountWithdrawalRequest(
        @NotNull(message = BankServiceConstants.validateAccountNumberMsg)
        @Positive(message = BankServiceConstants.validateAccountNumberNegativeMsg)
        Integer accountNumber,

        @NotNull(message = BankServiceConstants.validateAmountMsg)
        @Positive(message = BankServiceConstants.validateAmountNegativeMsg)
        @Min(value = 1, message = "Withdrawal amount can't be 0")
        Double withdrawalAmount
) {}
