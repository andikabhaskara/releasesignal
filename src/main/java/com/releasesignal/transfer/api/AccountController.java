package com.releasesignal.transfer.api;

import com.releasesignal.transfer.domain.AccountBalanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class AccountController {
    public final AccountBalanceService accountBalanceService;


    public AccountController(AccountBalanceService accountBalanceService) {
        this.accountBalanceService = accountBalanceService;
    }

    @GetMapping("/accounts/{accountId}/balance")
    public AccountBalanceResponse balance(@PathVariable String accountId) {
        BigDecimal balance = accountBalanceService.balanceOf(accountId);
        return new AccountBalanceResponse(accountId,balance);
    }
}