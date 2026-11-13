package com.releasesignal.transfer.api;

import com.releasesignal.transfer.domain.TransferCommand;
import com.releasesignal.transfer.domain.TransferExecution;
import com.releasesignal.transfer.domain.TransferResult;
import com.releasesignal.transfer.domain.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponseBody> transfer(
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,
            @Valid @RequestBody TransferRequestBody body) {

        TransferCommand command =
                new TransferCommand(body.fromAccountId(), body.toAccountId(), body.amount());
        TransferExecution execution = transferService.transfer(idempotencyKey, command);
        TransferResult result = execution.result();

        TransferResponseBody response = new TransferResponseBody(
                result.transferId(),
                result.fromAccountId(),
                result.toAccountId(),
                result.amount(),
                result.status().name());

        HttpStatus status = execution.replay() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }
}
