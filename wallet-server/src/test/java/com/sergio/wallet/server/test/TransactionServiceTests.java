package com.sergio.wallet.server.test;

import com.sergio.wallet.server.test.data.entity.Balance;
import com.sergio.wallet.server.test.data.entity.Transaction;
import com.sergio.wallet.server.test.data.repository.BalanceRepository;
import com.sergio.wallet.server.test.data.repository.TransactionRepository;
import com.sergio.wallet.server.test.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TransactionService.class, TransactionRepository.class})
public class TransactionServiceTests {

    private final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceTests.class);

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
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

    @Test
    public void when_Deposit_Is_Valid() {
        LOGGER.info("when_Deposit_Is_Valid");

        when(balanceRepository.findByUserIdAndCurrency(anyString(), anyString())).thenReturn(null);
        when(balanceRepository.save(any(Balance.class))).getMock();

        when(transactionRepository.save(any(Transaction.class))).getMock();

        assertEquals(TransactionService.RESPONSE_SUCCESSFUL, transactionService.doDeposit(userId, depositAmount, validCurrency));
    }

}
