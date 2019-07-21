package com.sergio.wallet.server.test.integration;

import com.sergio.wallet.server.test.grpc.GrpcWalletService;
import com.sergio.wallet.server.test.service.TransactionService;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.sergio.wallet.grpc.*;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceBlockingStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

/**
 * Main app's integration tests.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletServerApplicationTests {

    //region VARIABLES

    private final Logger LOGGER = LoggerFactory.getLogger(WalletServerApplicationTests.class);

    private WalletServiceBlockingStub testBlockingStub;

    @Autowired
    private GrpcWalletService grpcWalletService;

    //region TEST VARIABLES

    // Only values that are used in more than one test will be set here as variables.

    private final String userId = "testuser";

    private final long depositAmount = 100;

    private final long withdrawAmount = 20;

    private final String validCurrency = TransactionService.VALID_CURRENCIES.get(0);

    private final String invalidCurrency = "USD2";

    //endregion

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     * No need for @AfterAll annotated method.
     */
    @ClassRule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    //endregion

    @BeforeAll
    public void initTestClient() throws IOException {
        LOGGER.info("initTestClient");

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        this.grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(this.grpcWalletService).build().start());

        this.testBlockingStub = WalletServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                this.grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
    }

    //region TEST METHODS

    @Test
    @Order(1)
    public void contextLoads() {
        LOGGER.info("contextLoads");
    }

    @Test
    @Order(2)
    public void when_Deposit_Is_Valid() {
        LOGGER.info("when_Deposit_Is_Valid");

        TransactionResponse reply =
            this.testBlockingStub.deposit(TransactionRequest.newBuilder()
                .setUserId(userId)
                .setAmount(depositAmount)
                .setCurrency(validCurrency).build());

        Assertions.assertEquals(TransactionService.RESPONSE_SUCCESSFUL, reply.getMessage());
    }

    @Test
    @Order(3)
    public void when_Deposit_Is_Invalid() {
        LOGGER.info("when_Deposit_Is_Invalid");

        TransactionResponse reply =
            this.testBlockingStub.deposit(TransactionRequest.newBuilder()
                .setUserId(userId)
                .setAmount(depositAmount)
                .setCurrency(invalidCurrency).build());

        Assertions.assertEquals(TransactionService.RESPONSE_UNKNOWN_CURRENCY, reply.getMessage());
    }

    @Test
    @Order(4)
    public void when_Withdraw_Is_Valid() {
        LOGGER.info("when_Withdraw_Is_Valid");

        TransactionResponse reply =
            this.testBlockingStub.withdraw(TransactionRequest.newBuilder()
                .setUserId(userId)
                .setAmount(withdrawAmount)
                .setCurrency(validCurrency).build());

        Assertions.assertEquals(TransactionService.RESPONSE_SUCCESSFUL, reply.getMessage());
    }

    @Test
    @Order(5)
    public void when_Withdraw_Is_Invalid_Currency() {
        LOGGER.info("when_Withdraw_Is_Invalid_Currency");

        TransactionResponse reply =
            this.testBlockingStub.withdraw(TransactionRequest.newBuilder()
                .setUserId(userId)
                .setAmount(withdrawAmount)
                .setCurrency(invalidCurrency).build());

        Assertions.assertEquals(TransactionService.RESPONSE_UNKNOWN_CURRENCY, reply.getMessage());
    }

    @Test
    @Order(6)
    public void when_Withdraw_Is_Invalid_Funds() {
        LOGGER.info("when_Withdraw_Is_Invalid_Funds");

        TransactionResponse reply =
            this.testBlockingStub.withdraw(TransactionRequest.newBuilder()
                .setUserId(userId)
                .setAmount(1000)
                .setCurrency(validCurrency).build());

        Assertions.assertEquals(TransactionService.RESPONSE_INSUFFICIENT_FUNDS, reply.getMessage());
    }


    @Test
    @Order(7)
    public void when_getBalance_Is_Valid() {
        LOGGER.info("when_getBalance_Is_Valid");

        BalanceResponse reply =
            this.testBlockingStub.getBalance(BalanceRequest.newBuilder()
                .setUserId(userId)
                .build());

        Assertions.assertNotEquals(0, reply.getBalancesMap().size());
    }

    @Test
    @Order(8)
    public void when_getBalance_Is_Invalid() {
        LOGGER.info("when_getBalance_Is_Invalid");

        BalanceResponse reply =
            this.testBlockingStub.getBalance(BalanceRequest.newBuilder()
                .setUserId("missinguser")
                .build());

        Assertions.assertNotEquals(0, reply.getBalancesMap().size());
    }

    //endregion

}
