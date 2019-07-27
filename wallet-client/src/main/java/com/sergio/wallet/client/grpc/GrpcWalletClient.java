package com.sergio.wallet.client.grpc;

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

/**
 * Main grpc client class, encapsulating the grpc functionality and simplifies making requests to the grpc server.
 */
@Service
public class GrpcWalletClient {

    //region VARIABLES

    private final static Logger LOGGER = LoggerFactory.getLogger(GrpcWalletClient.class);

    @GrpcClient("local-grpc-server")
    private WalletServiceBlockingStub walletStub;

    //endregion

    //region private methods

    /**
     * Used by deposit and withdraw, unifies the request on both cases and avoids duplicating code.
     * @param request the data for the transaction.
     * @param isDeposit
     * @return
     */
    private String sendTransaction(TransactionRequest request, boolean isDeposit) {
        String message;

        try {
            final TransactionResponse response = isDeposit ? this.walletStub.deposit(request) :
                    this.walletStub.withdraw(request);
            message = response.getMessage();
        } catch (final StatusRuntimeException e) {
            message = "gRPC " + (isDeposit ? "deposit" : "withdraw")
                        + " request failed. | " + e.getStatus().getCode().name();
            LOGGER.error(message);
        }

        return message;
    }

    //endregion

    //region PUBLIC METHODS

    /**
     * Initiate a grpc request to the deposit endpoint, it will add funds to the user's currency
     * specific wallet.
     * @param userId
     * @param amount
     * @param currency
     * @return Empty String if successful or error message otherwise.
     */
    public String deposit(final String userId, final int amount, final String currency) {
        final TransactionRequest request = TransactionRequest
                .newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setCurrency(currency)
                .build();

        return sendTransaction(request, true);
    }

    /**
     * Initiate a grpc request to the withdraw endpoint, it will subtract funds from the user's
     * currency specific wallet.
     * @param userId
     * @param amount
     * @param currency
     * @return Empty String if successful or error message otherwise.
     */
    public String withdraw(final String userId, final int amount, final String currency) {
        final TransactionRequest request = TransactionRequest
                .newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setCurrency(currency)
                .build();

        return sendTransaction(request, false);
    }

    /**
     * Initiate a grpc request to the balance endpoint, it will receive the available funds per currency
     * from the user's wallet.
     * @param userId
     * @return Map with the funds per currency or empty map if an error happened or no funds.
     */
    public Map<String, Long> getBalance(final String userId) {
        final BalanceRequest request = BalanceRequest
                .newBuilder()
                .setUserId(userId)
                .build();
        try {
            final BalanceResponse response = this.walletStub.getBalance(request);
            return response.getBalancesMap();
        } catch (final StatusRuntimeException e) {
            LOGGER.error("gRPC balance request failed. | " + e.getStatus().getCode().name());
            return new HashMap<String, Long>();
        }
    }

    //endregion

}
