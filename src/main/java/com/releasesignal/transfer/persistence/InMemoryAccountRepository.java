package com.releasesignal.transfer.persistence;

import com.releasesignal.transfer.domain.InsufficientBalanceException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAccountRepository {
    private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();

    public BigDecimal getBalance(String accountId) {
        Objects.requireNonNull(accountId, "accountId"); //for easier debug: The Error Message: java.lang.NullPointerException: accountId. instead of Exception only if being removed
        return balances.computeIfAbsent(accountId, id -> BigDecimal.ZERO);
    }

    public void setBalance(String accountId, BigDecimal balance) {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(balance, "balance");
        if (balance.signum() < 0) {
            throw new IllegalArgumentException("balance must be non negative");
        }
        balances.put(accountId, balance);
    }

    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        Objects.requireNonNull(fromAccountId, "fromAccountId");
        Objects.requireNonNull(toAccountId, "toAccountId");
        Objects.requireNonNull(amount, "amount");

        if(amount.signum() < 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }

        BigDecimal fromAccountIdBalance = balances.get(fromAccountId);
        if (fromAccountIdBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(fromAccountId);
        }
        balances.put(fromAccountId, fromAccountIdBalance.subtract(amount));

        BigDecimal toAccountIdBalance = balances.get(toAccountId);
        balances.put(toAccountId, toAccountIdBalance.add(amount));
    }

}