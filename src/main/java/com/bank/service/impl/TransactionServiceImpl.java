package com.bank.service.impl;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.User;
import com.bank.entity.enums.TransactionStatus;
import com.bank.entity.enums.TransactionType;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.InvalidAccountException;
import com.bank.exception.InvalidAmountException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import com.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction deposit(String userEmail, BigDecimal amount) {
        validateAmount(amount);
        Account account = getAccountForEmail(userEmail);

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setSenderAccount(null);
        transaction.setReceiverAccount(account);
        transaction.setStatus(TransactionStatus.SUCCESS);

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction withdraw(String userEmail, BigDecimal amount) {
        validateAmount(amount);
        Account account = getAccountForEmail(userEmail);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for this withdrawal.");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setSenderAccount(account);
        transaction.setReceiverAccount(null);
        transaction.setStatus(TransactionStatus.SUCCESS);

        return transactionRepository.save(transaction);
    }

    /**
     * Transfers funds between two accounts atomically. Both the debit and the
     * credit happen within the same database transaction; if either the
     * validation or the persistence step fails, the entire operation rolls
     * back so the ledger can never end up half-applied.
     */
    @Override
    @Transactional
    public Transaction transfer(String userEmail, String receiverAccountNumber, BigDecimal amount) {
        validateAmount(amount);

        Account senderAccount = getAccountForEmail(userEmail);

        if (senderAccount.getAccountNumber().equals(receiverAccountNumber)) {
            throw new InvalidAccountException("You cannot transfer funds to your own account.");
        }

        Account receiverAccount = accountRepository.findByAccountNumberForUpdate(receiverAccountNumber)
                .orElseThrow(() -> new InvalidAccountException(
                        "Receiver account number '" + receiverAccountNumber + "' does not exist."));

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for this transfer.");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        // Record a single transaction row representing the transfer; both
        // sender and receiver are linked so it appears in both histories.
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.TRANSFER_SENT);
        transaction.setAmount(amount);
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setStatus(TransactionStatus.SUCCESS);

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionHistory(String userEmail) {
        Account account = getAccountForEmail(userEmail);
        return transactionRepository
                .findBySenderAccountIdOrReceiverAccountIdOrderByTransactionDateDesc(
                        account.getId(), account.getId());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
    }

    private Account getAccountForEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("No account found for user: " + email));
        return accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("No account found for user: " + email));
    }
}
