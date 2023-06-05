package za.co.reed.shaun.bankingserviceapi.controller;


import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountDepositRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.AccountWithdrawalRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.AccountResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.TransactionResponse;
import za.co.reed.shaun.bankingserviceapi.service.CurrentAccountService;

@RestController
@RequestMapping("/api/current/v1/")
@Validated
public class CurrentAccountController {
    private final CurrentAccountService currentAccountService;

    public CurrentAccountController(CurrentAccountService currentAccountService) {
        this.currentAccountService = currentAccountService;
    }

    @PostMapping(value = "open", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponse> createSavingsAccount(@Valid @RequestBody CurrentAccountRequest request) {
        return ResponseEntity.ok().body(currentAccountService.openCurrentAccount(request));
    }

    @PostMapping(value = "withdrawal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> withdrawalFromAccount(@Valid @RequestBody AccountWithdrawalRequest request) {
        return ResponseEntity.ok().body(currentAccountService.withdrawalFromCurrentAccount(request));
    }

    @PostMapping(value = "deposit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> depositToAccount(@Valid @RequestBody AccountDepositRequest request) {
        return ResponseEntity.ok().body(currentAccountService.depositToCurrentAccount(request));
    }
}
