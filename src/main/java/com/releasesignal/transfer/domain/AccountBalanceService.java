package com.releasesignal.transfer.domain;

import com.releasesignal.transfer.persistence.InMemoryAccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountBalanceService {
    private final InMemoryAccountRepository accounts;

    public AccountBalanceService(InMemoryAccountRepository accounts) {
        this.accounts = accounts;
    }

    public BigDecimal balanceOf(String accountId) {
        return accounts.getBalance(accountId);
    }
}
