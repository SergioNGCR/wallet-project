package com.sergio.wallet.client.services;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sergio.wallet.grpc.BalanceRequest;
import org.sergio.wallet.grpc.BalanceResponse;
import org.sergio.wallet.grpc.TransactionRequest;
import org.sergio.wallet.grpc.TransactionResponse;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceBlockingStub;

import java.util.HashMap;
import java.util.Map;

@Service
public class GrpcWalletService {

    private final static Logger LOGGER = LoggerFactory.getLogger(GrpcWalletService.class);

    @GrpcClient("local-grpc-server")
    private WalletServiceBlockingStub walletStub;

    private String sendTransaction(TransactionRequest request, boolean isDeposit) {
        String message;

        try {
            final TransactionResponse response = isDeposit ? this.walletStub.deposit(request) :
                                                            this.walletStub.withdraw(request);
            message = response.getMessage();
        } catch (final StatusRuntimeException e) {
            message = "FAILED with " + e.getStatus().getCode().name();
            LOGGER.error(message);
        }

        return message;
    }

    public String deposit(final String userId, final int amount, final String currency) {
        final TransactionRequest request = TransactionRequest
                .newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setCurrency(currency)
                .build();

        return sendTransaction(request, true);
    }

    public String withdraw(final String userId, final int amount, final String currency) {
        final TransactionRequest request = TransactionRequest
                .newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setCurrency(currency)
                .build();

        return sendTransaction(request, true);
    }

    public Map<String, Integer> getBalance(final String userId) {
        final BalanceRequest request = BalanceRequest
                .newBuilder()
                .setUserId(userId)
                .build();
        try {
            final BalanceResponse response = this.walletStub.getBalance(request);
            return response.getBalancesMap();
        } catch (final StatusRuntimeException e) {
            LOGGER.error("FAILED with " + e.getStatus().getCode().name());
            return new HashMap<String, Integer>();
        }
    }
}
