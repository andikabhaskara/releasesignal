package com.releasesignal.transfer.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record TransferCommand(String fromAccountId, String toAccountId, BigDecimal amount) {

    public TransferCommand {
        Objects.requireNonNull(fromAccountId);
        Objects.requireNonNull(toAccountId);
        Objects.requireNonNull(amount);
    }

    public boolean samePayload(TransferCommand other) {
        return fromAccountId.equals(other.fromAccountId())
                && toAccountId.equals(other.toAccountId())
                && amount.compareTo(other.amount) == 0;
    }
}
