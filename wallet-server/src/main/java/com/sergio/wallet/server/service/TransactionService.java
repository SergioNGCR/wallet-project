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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    private final static List<String> VALID_CURRENCIES = Arrays.asList("USD", "EUR", "GBP");

    public final static String RESPONSE_SUCCESSFUL = "";

    public final static String RESPONSE_UNKNOWN_CURRENCY = "Unknown currency";

    public final static String RESPONSE_INSUFFICIENT_FUNDS = "Insufficient funds";

    private final TransactionRepository transactionRepository;

    private final BalanceRepository balanceRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, BalanceRepository balanceRepository) {
        this.transactionRepository = transactionRepository;
        this.balanceRepository = balanceRepository;
    }

    /***
     * Method in charge of finding the right balance for the user and currency,
     * if it doesn't exists it creates a new one.
     * @param userId
     * @param currency
     * @param createIfAbsent Defines if the method should create a new balance for the user if it can't be found.
     * @return The balance in the specific currency for that user.
     */
    private Balance findOrCreateBalance(String userId, String currency, boolean createIfAbsent) {
        Balance balance = balanceRepository.findByUserIdAndCurrency(userId, currency);

        if (balance == null && createIfAbsent){
            balance = new Balance();
            balance.setUserId(userId);
            balance.setCurrency(currency);
            balance.setBalance(0);
            balance.setModified(LocalDateTime.now());
        }

        return balance;
    }

    public boolean isValidCurrency(String currency) {
        return VALID_CURRENCIES.contains(currency);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String doDeposit(String userId, long amount, String currency) {
        // Some transaction functionality based on the type.
        Balance balance = findOrCreateBalance(userId, currency, true);

        // Should probably validate if negative amount, though no such error message is defined in the exercise.
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setDeposit(amount);
        transaction.setCurrency(currency);
        transaction.setDate(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        balance.modifyBalance(amount, true);
        balance.setLastTransactionId(transaction.getId());

        balance = balanceRepository.save(balance);

        // Empty response equals to successful transaction.
        return RESPONSE_SUCCESSFUL;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String doWithdraw(String userId, long amount, String currency) {
        // Some transaction functionality based on the type.
        Balance balance = findOrCreateBalance(userId, currency, false);

        // First let's check if there are enough funds.
        if (balance.getBalance() - amount < 0) {
            return RESPONSE_INSUFFICIENT_FUNDS;
        }

        // Should probably validate if negative amount, though no such error message is defined in the exercise.
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setWithdraw(amount);
        transaction.setCurrency(currency);
        transaction.setDate(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        balance.modifyBalance(amount, false);
        balance.setLastTransactionId(transaction.getId());

        balance = balanceRepository.save(balance);

        // Empty response equals to successful transaction.
        return RESPONSE_SUCCESSFUL;
    }

    public Map<String, Long> getBalance(String userId) {
        // Some transaction functionality based on the type.
        Map<String, Long> balances = new HashMap<>();

        List<Balance> balanceList = balanceRepository.findAllByUserId(userId);

        balanceList.forEach(balance -> balances.put(balance.getCurrency(), balance.getBalance()));

        // Empty response equals to successful transaction.
        return balances;
    }

}
