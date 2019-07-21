package com.sergio.wallet.server.data.repository;

import com.sergio.wallet.server.data.entity.Balance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Basic interface to handle the repository for the Balance table.
 */
@Repository
public interface BalanceRepository extends CrudRepository<Balance, Long> {

    List<Balance> findAllByUserId(String userId);

    Balance findByUserIdAndCurrency(String userId, String currency);
}
