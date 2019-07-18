package com.sergio.wallet.server.service;

import com.sergio.wallet.server.data.repository.BalanceRepository;
import com.sergio.wallet.server.data.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

    public boolean isValidCurrency(String currency) {
        return VALID_CURRENCIES.contains(currency);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String doDeposit(String userId, long amount, String currency) {
        // Some transaction functionality based on the type.


        // Empty response equals to successful transaction.
        return RESPONSE_SUCCESSFUL;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String doWithdraw(String userId, long amount, String currency) {
        // Some transaction functionality based on the type.


        // Empty response equals to successful transaction.
        return RESPONSE_SUCCESSFUL;
    }

    public Map<String, Long> getBalance(String userId) {
        // Some transaction functionality based on the type.
        Map<String, Long> balances = new HashMap<>();



        // Empty response equals to successful transaction.
        return balances;
    }

}
