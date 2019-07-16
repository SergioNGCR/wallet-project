package com.sergio.wallet.server;

import com.sergio.wallet.server.services.GrpcWalletService;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sergio.wallet.grpc.TransactionRequest;
import org.sergio.wallet.grpc.TransactionResponse;
import org.sergio.wallet.grpc.WalletServiceGrpc;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceBlockingStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @BeforeClass
    public static void initTestClient() throws IOException {
        LOGGER.info("initTestClient");

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new GrpcWalletService()).build().start());

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

        TransactionResponse reply = testBlockingStub.deposit(TransactionRequest.newBuilder()
                                        .setUserId("sergio")
                                        .setAmount(100)
                                        .setCurrency("USD").build());

        assertEquals("", reply.getMessage());
    }

    @Test
    public void when_Withdraw_Is_Invalid() {
        LOGGER.info("when_Withdraw_Is_Invalid");

        TransactionResponse reply = testBlockingStub.withdraw(TransactionRequest.newBuilder()
                .setUserId("sergio")
                .setAmount(100)
                .setCurrency("USD1").build());

        // There should be some static variable for this messages.
        assertEquals("Unknown currency", reply.getMessage());
    }
}
