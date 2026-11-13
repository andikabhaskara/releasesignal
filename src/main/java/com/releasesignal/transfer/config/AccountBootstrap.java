package com.releasesignal.transfer.config;

import com.releasesignal.transfer.persistence.InMemoryAccountRepository;
import java.math.BigDecimal;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountBootstrap {

    public AccountBootstrap(InMemoryAccountRepository accounts) {
        accounts.setBalance("alice", new BigDecimal("100.00"));
        accounts.setBalance("bob", new BigDecimal("50.00"));
    }
}
