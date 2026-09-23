package com.bank.controller;

import com.bank.dto.AccountSummaryDto;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        AccountSummaryDto accountSummary = accountService.getAccountSummary(email);

        model.addAttribute("account", accountSummary);
        model.addAttribute("currentAccountNumber", accountSummary.getAccountNumber());
        model.addAttribute("recentTransactions",
                transactionService.getTransactionHistory(email).stream().limit(5).toList());

        return "dashboard";
    }
}
