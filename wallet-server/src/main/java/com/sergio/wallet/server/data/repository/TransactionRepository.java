package com.sergio.wallet.server.data.repository;

import com.sergio.wallet.server.data.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Basic interface to handle the repository for the Transaction table.
 */
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    // Currently no additional methods needed.

    //List<Transaction> findAllByUserId(String userId);

    //List<Transaction> findAllByUserIdAndCurrency(String userId, String currency);
}
