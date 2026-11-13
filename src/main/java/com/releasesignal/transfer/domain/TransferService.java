package com.releasesignal.transfer.domain;

import com.releasesignal.transfer.persistence.InMemoryAccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransferService {

    private final InMemoryAccountRepository accounts;
    private final Object transferLock = new Object();


    public TransferService(InMemoryAccountRepository accounts) {
        this.accounts = accounts;
    }

    private final Map<String, IdempotencyEntry> idempotencyByKey = new ConcurrentHashMap<>();

    public TransferExecution transfer(String idempotencyKey, TransferCommand command) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
        if (command.fromAccountId().equals(command.toAccountId())) {
            throw new IllegalArgumentException("from and to accounts must differ");
        }

        synchronized (transferLock) {
            IdempotencyEntry existing = idempotencyByKey.get(idempotencyKey);
            if (existing != null) {
                if (!existing.command().samePayload(command)) {
                    throw new IdempotencyConflictException(
                            "Idempotency-Key was already used with a different request body");
                }
                return new TransferExecution(true, existing.result());
            }

            accounts.transfer(command.fromAccountId(), command.toAccountId(), command.amount());
            String transferId = UUID.randomUUID().toString();
            TransferResult result = new TransferResult(
                    transferId,
                    command.fromAccountId(),
                    command.toAccountId(),
                    command.amount(),
                    TransferResult.TransferStatus.COMPLETED);
            idempotencyByKey.put(idempotencyKey, new IdempotencyEntry(command, result));
            return new TransferExecution(false, result);
        }
    }

    public BigDecimal balanceOf(String accountId) {
        return accounts.getBalance(accountId);
    }
}
