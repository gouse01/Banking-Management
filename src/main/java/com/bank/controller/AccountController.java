package com.bank.controller;

import com.bank.dto.AccountSummaryDto;
import com.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account")
    public String accountDetails(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        AccountSummaryDto accountSummary = accountService.getAccountSummary(userDetails.getUsername());
        model.addAttribute("account", accountSummary);
        return "account";
    }
}
