package com.sergio.wallet.server.service;

import com.sergio.wallet.server.data.entity.Balance;
import com.sergio.wallet.server.data.entity.Transaction;
import com.sergio.wallet.server.data.repository.BalanceRepository;
import com.sergio.wallet.server.data.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {

    //region VARIABLES

    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    private final BalanceRepository balanceRepository;

    public final static List<String> VALID_CURRENCIES =
            Collections.unmodifiableList(Arrays.asList("USD", "EUR", "GBP"));

    public final static String RESPONSE_SUCCESSFUL = "";

    public final static String RESPONSE_UNKNOWN_CURRENCY = "Unknown currency";

    public final static String RESPONSE_INSUFFICIENT_FUNDS = "Insufficient funds";

    //endregion

    //region CONSTRUCTORS

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, BalanceRepository balanceRepository) {
        this.transactionRepository = transactionRepository;
        this.balanceRepository = balanceRepository;
    }

    //endregion

    //region PRIVATE METHOD

    /**
     * Method in charge of finding the right balance for the user and currency,
     * if it doesn't exists it creates a new one.
     * @param userId
     * @param currency
     * @return The balance in the specific currency for that user.
     */
    private Balance findOrCreateBalance(String userId, String currency) {
        Balance balance = balanceRepository.findByUserIdAndCurrency(userId, currency);

        if (balance == null){
            balance = new Balance();
            balance.setUserId(userId);
            balance.setCurrency(currency);
            balance.setBalance(0);
            balance.setModified(LocalDateTime.now());
        }

        return balance;
    }

    /**
     * Method in charge of the logic for executing transactions, it will treat deposits and withdraws
     * differently to modify the balance accordingly.
     * It will create a new transaction row in the Transaction table.
     * It will update or create a row in the Balance table.
     * @param userId
     * @param amount It will be deposit or withdraw type depending on the isDeposit parameter.
     * @param currency
     * @param isDeposit if called to execute a deposit or a withdraw.
     * @return
     */
    private String executeTransaction(String userId, long amount, String currency, boolean isDeposit) {
        // First check the currency is a valid one.
        if(!VALID_CURRENCIES.contains(currency)) {
            return TransactionService.RESPONSE_UNKNOWN_CURRENCY;
        }

        // Let's retrieve the balance for the user and currency, create if absent only for deposits.
        Balance balance = findOrCreateBalance(userId, currency);

        // Should probably validate if negative amount, though no such error message is defined in the exercise.

        // If this transaction is a withdraw, first let's check if there are enough funds.
        if (!isDeposit && balance.getBalance() - amount < 0) {
            return RESPONSE_INSUFFICIENT_FUNDS;
        }

        // Let's define a transaction and set all the values.
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setCurrency(currency);
        transaction.setDate(LocalDateTime.now());

        // Set the right amount type for the transaction.
        if (isDeposit) {
            transaction.setDeposit(amount);
        } else {
            transaction.setWithdraw(amount);
        }

        // Perform insert to the transaction table.
        transaction = transactionRepository.save(transaction);

        // Simple necessary updates to the balance data.
        balance.modifyBalance(amount, isDeposit);
        balance.setLastTransactionId(transaction.getId());

        // Update row or insert the balance into balance table.
        balance = balanceRepository.save(balance);

        // Empty response equals to successful transaction.
        return RESPONSE_SUCCESSFUL;
    }

    //endregion

    //region PUBLIC METHODS

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String doDeposit(String userId, long amount, String currency) {
        // Let's execute the transaction and return the result.
        return executeTransaction(userId, amount, currency, true);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String doWithdraw(String userId, long amount, String currency) {
        // Let's execute the transaction and return the result.
        return executeTransaction(userId, amount, currency, false);
    }

    public Map<String, Long> getBalance(String userId) {
        // Some transaction functionality based on the type.
        Map<String, Long> balances = new HashMap<>();

        List<Balance> balanceList = balanceRepository.findAllByUserId(userId);

        balanceList.forEach(balance -> balances.putIfAbsent(balance.getCurrency(), balance.getBalance()));

        // Empty response equals to successful transaction.
        return balances;
    }

    //endregion

}
