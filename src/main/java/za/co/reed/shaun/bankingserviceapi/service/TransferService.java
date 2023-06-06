package za.co.reed.shaun.bankingserviceapi.service;

import za.co.reed.shaun.bankingserviceapi.model.request.TransferRequest;
import za.co.reed.shaun.bankingserviceapi.model.response.TransferResponse;

public interface TransferService {
    TransferResponse transferMoney(TransferRequest transferRequest);
}
