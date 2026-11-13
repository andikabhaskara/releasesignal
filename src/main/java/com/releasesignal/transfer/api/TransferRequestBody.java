package com.releasesignal.transfer.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequestBody(
        @NotBlank(message = "fromAccountId is required") String fromAccountId,
        @NotBlank(message = "toAccountId is required") String toAccountId,
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0") BigDecimal amount) {}
