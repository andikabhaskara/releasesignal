package com.releasesignal.transfer.api;

import java.math.BigDecimal;

public record TransferResponseBody(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String status) {}
