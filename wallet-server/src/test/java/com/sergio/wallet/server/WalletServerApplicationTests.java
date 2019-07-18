package com.sergio.wallet.server;

import com.sergio.wallet.server.data.repository.TransactionRepository;
import com.sergio.wallet.server.grpc.GrpcWalletService;
import com.sergio.wallet.server.service.TransactionService;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sergio.wallet.grpc.*;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceBlockingStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class WalletServerApplicationTests {

    private final static Logger LOGGER = LoggerFactory.getLogger(WalletServerApplicationTests.class);

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     * No need for @AfterAll annotated method.
     */
    @ClassRule
    public final static GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private static WalletServiceBlockingStub testBlockingStub;

    // TODO: We need to mock this dependency.
    @Autowired
    private static TransactionService transactionService;

    @BeforeClass
    public static void initTestClient() throws IOException {
        LOGGER.info("initTestClient");

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new GrpcWalletService(transactionService)).build().start());

        testBlockingStub = WalletServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
    }

    @Test
    public void contextLoads() {
        LOGGER.info("contextLoads");
    }

    @Test
    public void when_Deposit_Is_Valid() {
        LOGGER.info("when_Deposit_Is_Valid");

        TransactionResponse reply =
            testBlockingStub.deposit(TransactionRequest.newBuilder()
                .setUserId("testuser")
                .setAmount(100)
                .setCurrency("USD").build());

        assertEquals(TransactionService.RESPONSE_SUCCESSFUL, reply.getMessage());
    }

    @Test
    public void when_Deposit_Is_Invalid() {
        LOGGER.info("when_Deposit_Is_Invalid");

        TransactionResponse reply =
            testBlockingStub.deposit(TransactionRequest.newBuilder()
                .setUserId("testuser")
                .setAmount(100)
                .setCurrency("USD2").build());

        assertEquals(TransactionService.RESPONSE_UNKNOWN_CURRENCY, reply.getMessage());
    }

    @Test
    public void when_Withdraw_Is_Valid() {
        LOGGER.info("when_Withdraw_Is_Valid");

        TransactionResponse reply =
            testBlockingStub.withdraw(TransactionRequest.newBuilder()
                .setUserId("testuser")
                .setAmount(100)
                .setCurrency("USD").build());

        assertEquals(TransactionService.RESPONSE_SUCCESSFUL, reply.getMessage());
    }

    @Test
    public void when_Withdraw_Is_Invalid_Currency() {
        LOGGER.info("when_Withdraw_Is_Invalid_Currency");

        TransactionResponse reply =
            testBlockingStub.withdraw(TransactionRequest.newBuilder()
                .setUserId("testuser")
                .setAmount(100)
                .setCurrency("USD1").build());

        assertEquals(TransactionService.RESPONSE_UNKNOWN_CURRENCY, reply.getMessage());
    }

    @Test
    public void when_Withdraw_Is_Invalid_Funds() {
        LOGGER.info("when_Withdraw_Is_Invalid_Funds");

        TransactionResponse reply =
            testBlockingStub.withdraw(TransactionRequest.newBuilder()
                .setUserId("testuser")
                .setAmount(1000000)
                .setCurrency("USD").build());

        // TODO: Make sure to "activate" this test case once the functionality is available to mock.
        //assertEquals(GrpcWalletService.RESPONSE_INSUFFICIENT_FUNDS, reply.getMessage());
    }


    @Test
    public void when_getBalance_Is_Valid() {
        LOGGER.info("when_getBalance_Is_Valid");

        BalanceResponse reply =
                testBlockingStub.getBalance(BalanceRequest.newBuilder()
                        .setUserId("testuser")
                        .build());

        assertNotEquals(0, reply.getBalancesMap().size());
    }

    @Test
    public void when_getBalance_Is_Invalid() {
        LOGGER.info("when_getBalance_Is_Invalid");

        BalanceResponse reply =
                testBlockingStub.getBalance(BalanceRequest.newBuilder()
                        .setUserId("missingUser")
                        .build());

        // TODO: Make sure to "activate" this test case once the functionality is available to mock.
        //assertEquals(0, reply.getBalancesMap().size());
    }

}
