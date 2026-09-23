package com.bank.controller;

import com.bank.dto.DepositDto;
import com.bank.dto.TransferDto;
import com.bank.dto.WithdrawDto;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    // -------------------- DEPOSIT --------------------

    @GetMapping("/deposit")
    public String depositPage(Model model) {
        model.addAttribute("depositDto", new DepositDto());
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@Valid @ModelAttribute("depositDto") DepositDto depositDto,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "deposit";
        }

        try {
            transactionService.deposit(userDetails.getUsername(), depositDto.getAmount());
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "deposit";
        }

        model.addAttribute("successMessage", "Deposit successful!");
        model.addAttribute("depositDto", new DepositDto());
        return "deposit";
    }

    // -------------------- WITHDRAW --------------------

    @GetMapping("/withdraw")
    public String withdrawPage(Model model) {
        model.addAttribute("withdrawDto", new WithdrawDto());
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@Valid @ModelAttribute("withdrawDto") WithdrawDto withdrawDto,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "withdraw";
        }

        try {
            transactionService.withdraw(userDetails.getUsername(), withdrawDto.getAmount());
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "withdraw";
        }

        model.addAttribute("successMessage", "Withdrawal successful!");
        model.addAttribute("withdrawDto", new WithdrawDto());
        return "withdraw";
    }

    // -------------------- TRANSFER --------------------

    @GetMapping("/transfer")
    public String transferPage(Model model) {
        model.addAttribute("transferDto", new TransferDto());
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@Valid @ModelAttribute("transferDto") TransferDto transferDto,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "transfer";
        }

        try {
            transactionService.transfer(
                    userDetails.getUsername(),
                    transferDto.getReceiverAccountNumber(),
                    transferDto.getAmount());
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "transfer";
        }

        model.addAttribute("successMessage", "Transfer successful!");
        model.addAttribute("transferDto", new TransferDto());
        return "transfer";
    }

    // -------------------- TRANSACTION HISTORY --------------------

    @GetMapping("/transactions")
    public String transactionHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("transactions", transactionService.getTransactionHistory(userDetails.getUsername()));
        model.addAttribute("currentAccountNumber",
                accountService.getAccountSummary(userDetails.getUsername()).getAccountNumber());
        return "transactions";
    }
}
