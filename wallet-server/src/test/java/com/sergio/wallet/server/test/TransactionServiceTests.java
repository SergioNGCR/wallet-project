package com.sergio.wallet.server.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sergio.wallet.server.data.entity.Balance;
import com.sergio.wallet.server.data.entity.Transaction;
import com.sergio.wallet.server.data.repository.BalanceRepository;
import com.sergio.wallet.server.data.repository.TransactionRepository;
import com.sergio.wallet.server.service.TransactionService;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


/**
 * Simple unit test class for validating the methods from TransactionService class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionService.class)
public class TransactionServiceTests {

    //region VARIABLES

    private final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceTests.class);

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private BalanceRepository balanceRepository;

    @Autowired
    private TransactionService transactionService;

    //region TEST VARIABLES

    // Only values that are used in more than one test will be set here as variables.

    private final String userId = "testuser";

    private final long depositAmount = 100;

    private final long withdrawAmount = 20;

    private final String validCurrency = TransactionService.VALID_CURRENCIES.get(0);

    private final String invalidCurrency = "USD2";

    //endregion

    //endregion

    //region TEST METHODS

    @Test
    public void when_Deposit_Is_Valid() {
        LOGGER.info("when_Deposit_Is_Valid");

        Balance userBalance = new Balance();
        userBalance.setUserId(userId);
        userBalance.setBalance(depositAmount);
        userBalance.setCurrency(validCurrency);
        userBalance.setId(1);
        userBalance.setLastTransactionId(1);

        when(balanceRepository.findByUserIdAndCurrency(anyString(), anyString())).thenReturn(null);
        when(balanceRepository.save(any(Balance.class))).thenReturn(userBalance);

        Transaction deposit = new Transaction();
        deposit.setUserId(userId);
        deposit.setDeposit(depositAmount);
        deposit.setCurrency(validCurrency);
        deposit.setId(1);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(deposit);

        assertThat(transactionService.doDeposit(userId, depositAmount, validCurrency),
                is(equalTo(TransactionService.RESPONSE_SUCCESSFUL)));
    }

    @Test
    public void when_Deposit_Is_Invalid() {
        LOGGER.info("when_Deposit_Is_Invalid");

        assertThat(transactionService.doDeposit(userId, depositAmount, invalidCurrency),
                is(equalTo(TransactionService.RESPONSE_UNKNOWN_CURRENCY)));
    }

    @Test
    public void when_Withdraw_Is_Valid() {
        LOGGER.info("when_Withdraw_Is_Valid");

        Balance userBalance = new Balance();
        userBalance.setUserId(userId);
        userBalance.setBalance(depositAmount);
        userBalance.setCurrency(validCurrency);
        userBalance.setId(1);
        userBalance.setLastTransactionId(1);

        when(balanceRepository.findByUserIdAndCurrency(anyString(), anyString())).thenReturn(userBalance);
        when(balanceRepository.save(any(Balance.class))).thenReturn(userBalance);

        Transaction withdraw = new Transaction();
        withdraw.setUserId(userId);
        withdraw.setWithdraw(withdrawAmount);
        withdraw.setCurrency(validCurrency);
        withdraw.setId(2);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(withdraw);

        assertThat(transactionService.doWithdraw(userId, withdrawAmount, validCurrency),
                is(equalTo(TransactionService.RESPONSE_SUCCESSFUL)));
    }

    @Test
    public void when_Withdraw_Is_Invalid_Currency() {
        LOGGER.info("when_Withdraw_Is_Invalid_Currency");

        assertThat(transactionService.doWithdraw(userId, withdrawAmount, invalidCurrency),
                is(equalTo(TransactionService.RESPONSE_UNKNOWN_CURRENCY)));
    }

    @Test
    public void when_Withdraw_Is_Invalid_Funds() {
        LOGGER.info("when_Withdraw_Is_Invalid_Funds");

        Balance userBalance = new Balance();
        userBalance.setUserId(userId);
        userBalance.setBalance(depositAmount);
        userBalance.setCurrency(validCurrency);
        userBalance.setId(1);
        userBalance.setLastTransactionId(1);

        when(balanceRepository.findByUserIdAndCurrency(anyString(), anyString())).thenReturn(userBalance);

        assertThat(transactionService.doWithdraw(userId, 1000, validCurrency),
                    is(equalTo(TransactionService.RESPONSE_INSUFFICIENT_FUNDS)));
    }


    @Test
    public void when_getBalance_Is_Valid() {
        LOGGER.info("when_getBalance_Is_Valid");

        List<Balance> userBalances = new ArrayList<>();

        Balance validCurrencyBalance = new Balance();
        validCurrencyBalance.setUserId(userId);
        validCurrencyBalance.setBalance(0);
        validCurrencyBalance.setCurrency(validCurrency);
        validCurrencyBalance.setId(1);
        validCurrencyBalance.setLastTransactionId(2);

        userBalances.add(validCurrencyBalance);

        when(balanceRepository.findAllByUserId(anyString())).thenReturn(userBalances);

        assertThat(transactionService.getBalance(userId).size(), is(not(equalTo(0))));
    }

    @Test
    public void when_getBalance_Is_Invalid() {
        LOGGER.info("when_getBalance_Is_Invalid");

        when(balanceRepository.findAllByUserId(anyString())).thenReturn(new ArrayList<>());

        assertThat(transactionService.getBalance("missinguser").size(), is(equalTo(0)));
    }

    //endregion


}
