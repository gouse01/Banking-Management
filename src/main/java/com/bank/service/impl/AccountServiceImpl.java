package com.bank.service.impl;

import com.bank.dto.AccountSummaryDto;
import com.bank.entity.Account;
import com.bank.entity.User;
import com.bank.entity.enums.AccountType;
import com.bank.exception.AccountNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public Account createAccountForUser(User user) {
        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        return accountRepository.save(account);
    }

    @Override
    public Account getAccountByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("No account found for user: " + email));
        return accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("No account found for user: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountSummaryDto getAccountSummary(String email) {
        Account account = getAccountByEmail(email);
        return new AccountSummaryDto(
                account.getAccountNumber(),
                account.getUser().getName(),
                account.getAccountType(),
                account.getBalance()
        );
    }

    /**
     * Generates a random 10-digit account number and verifies uniqueness
     * against the database before returning it.
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            long number = 1000000000L + (long) (RANDOM.nextDouble() * 8999999999L);
            accountNumber = String.valueOf(number);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
