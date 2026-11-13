package com.releasesignal.transfer.domain;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String accountId) {
        super("Insufficient balance on accountId: " + accountId);
    }
}