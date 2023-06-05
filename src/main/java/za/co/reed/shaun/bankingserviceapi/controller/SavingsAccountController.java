package za.co.reed.shaun.bankingserviceapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import za.co.reed.shaun.bankingserviceapi.entity.AccountInformation;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.service.SavingsAccountService;

@RestController
@RequestMapping("/api/savings/v1/")
@Validated
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;

    public SavingsAccountController(SavingsAccountService savingsAccountService) {
        this.savingsAccountService = savingsAccountService;
    }

    @PostMapping(value = "open", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponse> createSavingsAccount(@Valid @RequestBody SavingsAccountRequest request) {
        return ResponseEntity.ok().body(savingsAccountService.openSavingsAccount(request));
    }

    @PostMapping(value = "withdrawal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> withdrawalFromAccount(@Valid @RequestBody AccountWithdrawalRequest request) {
        return ResponseEntity.ok().body(savingsAccountService.withdrawalFromSavingsAccount(request));
    }

    @PostMapping(value = "deposit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> depositToAccount(@Valid @RequestBody AccountDepositRequest request) {
        return ResponseEntity.ok().body(savingsAccountService.depositToSavingsAccount(request));
    }
}
