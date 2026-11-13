package com.releasesignal.transfer.domain;

public record TransferExecution(boolean replay, TransferResult result) {}
