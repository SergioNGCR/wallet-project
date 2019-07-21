package com.sergio.wallet.server.data.repository;

import com.sergio.wallet.server.data.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Basic interface to handle the repository for the Transaction table.
 */
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAllByUserId(String userId);

    List<Transaction> findAllByUserIdAndCurrency(String userId, String currency);
}
