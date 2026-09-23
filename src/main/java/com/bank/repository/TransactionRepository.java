package com.bank.repository;

import com.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select distinct t from Transaction t " +
            "left join fetch t.senderAccount sa " +
            "left join fetch t.receiverAccount ra " +
            "where sa.id = :senderAccountId or ra.id = :receiverAccountId " +
            "order by t.transactionDate desc")
    List<Transaction> findBySenderAccountIdOrReceiverAccountIdOrderByTransactionDateDesc(
            @Param("senderAccountId") Long senderAccountId,
            @Param("receiverAccountId") Long receiverAccountId);
}
