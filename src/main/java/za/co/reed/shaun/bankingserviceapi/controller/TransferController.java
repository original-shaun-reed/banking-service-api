package za.co.reed.shaun.bankingserviceapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.reed.shaun.bankingserviceapi.model.request.TransferRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.TransferResponse;
import za.co.reed.shaun.bankingserviceapi.service.TransferService;

@RestController
@RequestMapping("/api/transfer/v1")
@Validated
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransferResponse> transferMoney(@Valid @RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(transferService.transferMoney(transferRequest));
    }
}
