package com.bank.service;

import com.bank.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Transaction deposit(String userEmail, BigDecimal amount);

    Transaction withdraw(String userEmail, BigDecimal amount);

    Transaction transfer(String userEmail, String receiverAccountNumber, BigDecimal amount);

    List<Transaction> getTransactionHistory(String userEmail);
}
