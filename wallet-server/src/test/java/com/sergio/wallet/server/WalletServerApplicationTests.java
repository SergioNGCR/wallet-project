package com.sergio.wallet.server;

import com.sergio.wallet.server.services.GrpcWalletService;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.sergio.wallet.grpc.TransactionRequest;
import org.sergio.wallet.grpc.TransactionResponse;
import org.sergio.wallet.grpc.WalletServiceGrpc;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceBlockingStub;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WalletServerApplicationTests {

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     * No need for @AfterAll annotated method.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private WalletServiceBlockingStub testBlockingStub;

    @BeforeAll
    void initTestClient() throws IOException {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(new GrpcWalletService()).build().start());

        this.testBlockingStub = WalletServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void when_Deposit_Is_Valid() {
        TransactionResponse reply = this.testBlockingStub.deposit(TransactionRequest.newBuilder()
                                        .setUserId("sergio")
                                        .setAmount(100)
                                        .setCurrency("USD").build());

        assertEquals("", reply.getMessage());
    }
}
