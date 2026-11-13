package com.releasesignal.transfer.domain;

import java.math.BigDecimal;

public record TransferResult(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        TransferStatus status) {

    public enum TransferStatus {
        COMPLETED
    }
}
