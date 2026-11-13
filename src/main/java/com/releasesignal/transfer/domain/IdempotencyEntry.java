package com.releasesignal.transfer.domain;

public record IdempotencyEntry(TransferCommand command, TransferResult result) {}
