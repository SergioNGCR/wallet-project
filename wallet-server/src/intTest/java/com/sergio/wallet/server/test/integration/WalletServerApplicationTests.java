package com.sergio.wallet.server.test.integration;

import com.sergio.wallet.server.grpc.GrpcWalletService;
import com.sergio.wallet.server.service.TransactionService;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

import org.junit.*;

import org.junit.runner.RunWith;

import org.junit.runners.MethodSorters;
import org.sergio.wallet.grpc.*;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceBlockingStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Main app's integration tests, needed for validating that the grpc server, transaction service
 * and database communicate and work appropriately.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WalletServerApplicationTests {

    //region VARIABLES

    // Simple flag to avoid setting up the gRPC server multiple times, can't use Autowired with BeforeClass.
    private static boolean beansLoaded = false;

    private final Logger LOGGER = LoggerFactory.getLogger(WalletServerApplicationTests.class);

    // Simple grpc blocking stub as client side for testing.
    private static WalletServiceBlockingStub testClientBlockingStub;

    @Autowired
    private GrpcWalletService grpcWalletService;

    //region TEST VARIABLES

    // Only values that are used in more than one test will be set here as variables.

    private final String userId = "1";

    private final long depositAmount = 100;

    private final long withdrawAmount = 200;

    private final String currencyUsd = TransactionService.VALID_CURRENCIES.get(0);

    private final String currencyEur = TransactionService.VALID_CURRENCIES.get(1);

    //endregion

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     * No need for @After or @AfterClass annotated methods to clean up started servers.
     */
    @ClassRule
    public final static GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    //endregion

    /**
     * Method used for initializing the test grpc server and a basic grpc client for integration tests with
     * the implementation of the grpc classes.
     * @throws IOException
     */
    @Before
    public void initTestClient() throws IOException {
        if (!beansLoaded) {
            LOGGER.info("initTestClient");

            // Generate a unique in-process server name.
            String serverName = InProcessServerBuilder.generateName();

            // Create a server, add service, start, and register for automatic graceful shutdown.
            grpcCleanup.register(InProcessServerBuilder
                    .forName(serverName).directExecutor().addService(this.grpcWalletService).build().start());

            testClientBlockingStub = WalletServiceGrpc.newBlockingStub(
                    // Create a client channel and register for automatic graceful shutdown.
                    grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

            beansLoaded = true;
        }
    }

    //region TEST METHODS

    @Test
    public void contextLoads() {
        LOGGER.info("contextLoads");
    }

    @Test
    public void test_01_withdrawal_USD_200() {
        LOGGER.info("test_1_withdrawal_USD_200");

        TransactionResponse reply =
            testClientBlockingStub.withdraw(TransactionRequest.newBuilder()
                .setUserId(userId)
                .setAmount(withdrawAmount)
                .setCurrency(currencyUsd).build());

        assertEquals(TransactionService.RESPONSE_INSUFFICIENT_FUNDS, reply.getMessage());
    }

    @Test
    public void test_02_deposit_USD_100() {
        LOGGER.info("test_2_deposit_USD_100");

        TransactionResponse reply =
            testClientBlockingStub.deposit(TransactionRequest.newBuilder()
                .setUserId(userId)
                .setAmount(depositAmount)
                .setCurrency(currencyUsd).build());

        assertEquals(TransactionService.RESPONSE_SUCCESSFUL, reply.getMessage());
    }

    @Test
    public void test_03_getBalances_user_1() {
        LOGGER.info("test_3_getBalances_user_1");

        BalanceResponse reply =
                testClientBlockingStub.getBalance(BalanceRequest.newBuilder()
                        .setUserId(userId)
                        .build());

        // We should have 1 balance available.
        assertEquals(1, reply.getBalancesMap().size());

        // At this point we should have USD 100 positive balance.
        assertEquals(100, reply.getBalancesMap().get(currencyUsd).longValue());
    }

    @Test
    public void test_04_withdrawal_USD_200() {
        LOGGER.info("test_4_withdrawal_USD_200");

        TransactionResponse reply =
                testClientBlockingStub.withdraw(TransactionRequest.newBuilder()
                        .setUserId(userId)
                        .setAmount(withdrawAmount)
                        .setCurrency(currencyUsd).build());

        assertEquals(TransactionService.RESPONSE_INSUFFICIENT_FUNDS, reply.getMessage());
    }

    @Test
    public void test_05_deposit_EUR_100() {
        LOGGER.info("test_5_deposit_EUR_100");

        TransactionResponse reply =
                testClientBlockingStub.deposit(TransactionRequest.newBuilder()
                        .setUserId(userId)
                        .setAmount(depositAmount)
                        .setCurrency(currencyEur).build());

        assertEquals(TransactionService.RESPONSE_SUCCESSFUL, reply.getMessage());
    }

    @Test
    public void test_06_getBalances_user_1() {
        LOGGER.info("test_6_getBalances_user_1");

        BalanceResponse reply =
                testClientBlockingStub.getBalance(BalanceRequest.newBuilder()
                        .setUserId(userId)
                        .build());

        // We should have now 2 different currencies.
        assertEquals(2, reply.getBalancesMap().size());

        // Also at this point we should have USD 100 positive balance.
        assertEquals(100, reply.getBalancesMap().get(currencyUsd).longValue());

        // Also at this point we should have EUR 100 positive balance.
        assertEquals(100, reply.getBalancesMap().get(currencyEur).longValue());
    }

    @Test
    public void test_07_withdrawal_USD_200() {
        LOGGER.info("test_7_withdrawal_USD_200");

        TransactionResponse reply =
                testClientBlockingStub.withdraw(TransactionRequest.newBuilder()
                        .setUserId(userId)
                        .setAmount(withdrawAmount)
                        .setCurrency(currencyUsd).build());

        assertEquals(TransactionService.RESPONSE_INSUFFICIENT_FUNDS, reply.getMessage());
    }

    @Test
    public void test_08_deposit_USD_100() {
        LOGGER.info("test_8_deposit_USD_100");

        TransactionResponse reply =
                testClientBlockingStub.deposit(TransactionRequest.newBuilder()
                        .setUserId(userId)
                        .setAmount(depositAmount)
                        .setCurrency(currencyUsd).build());

        assertEquals(TransactionService.RESPONSE_SUCCESSFUL, reply.getMessage());
    }

    @Test
    public void test_09_getBalances_user_1() {
        LOGGER.info("test_6_getBalances_user_1");

        BalanceResponse reply =
                testClientBlockingStub.getBalance(BalanceRequest.newBuilder()
                        .setUserId(userId)
                        .build());

        // We should have now 2 different currencies.
        assertEquals(2, reply.getBalancesMap().size());

        // Also at this point we should have USD 100 positive balance.
        assertEquals(200, reply.getBalancesMap().get(currencyUsd).longValue());

        // Also at this point we should have EUR 100 positive balance.
        assertEquals(100, reply.getBalancesMap().get(currencyEur).longValue());
    }

    @Test
    public void test_10_withdrawal_USD_200() {
        LOGGER.info("test_10_withdrawal_USD_200");

        TransactionResponse reply =
                testClientBlockingStub.withdraw(TransactionRequest.newBuilder()
                        .setUserId(userId)
                        .setAmount(withdrawAmount)
                        .setCurrency(currencyUsd).build());

        assertEquals(TransactionService.RESPONSE_SUCCESSFUL, reply.getMessage());
    }

    @Test
    public void test_11_getBalances_user_1() {
        LOGGER.info("test_11_getBalances_user_1");

        BalanceResponse reply =
                testClientBlockingStub.getBalance(BalanceRequest.newBuilder()
                        .setUserId(userId)
                        .build());

        // We should have now 2 different currencies.
        assertEquals(2, reply.getBalancesMap().size());

        // Also at this point we should have USD 0 balance.
        assertEquals(0, reply.getBalancesMap().get(currencyUsd).longValue());

        // Also at this point we should have EUR 100 positive balance.
        assertEquals(100, reply.getBalancesMap().get(currencyEur).longValue());
    }

    @Test
    public void test_12_withdrawal_USD_200() {
        LOGGER.info("test_12_withdrawal_USD_200");

        TransactionResponse reply =
                testClientBlockingStub.withdraw(TransactionRequest.newBuilder()
                        .setUserId(userId)
                        .setAmount(withdrawAmount)
                        .setCurrency(currencyUsd).build());

        assertEquals(TransactionService.RESPONSE_INSUFFICIENT_FUNDS, reply.getMessage());
    }

    //endregion

}
