package com.releasesignal.transfer.api;

import java.math.BigDecimal;

public record AccountBalanceResponse(String accountId, BigDecimal balance) {}
