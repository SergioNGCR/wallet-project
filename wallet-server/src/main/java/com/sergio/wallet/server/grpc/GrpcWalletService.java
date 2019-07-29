package com.sergio.wallet.server.grpc;

import com.sergio.wallet.server.service.TransactionService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.sergio.wallet.grpc.*;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceImplBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Main gRPC Service class that handles the endpoints for the wallet server, it provides the functionality
 * for making deposits, withdraws and retrieving an user's balance for all currencies.
 */
@GrpcService
public class GrpcWalletService extends WalletServiceImplBase {

    //region VARIABLES

    private final static Logger LOGGER = LoggerFactory.getLogger(GrpcWalletService.class);

    private final TransactionService transactionService;

    //endregion

    //region CONSTRUCTORS

    @Autowired
    public GrpcWalletService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    //endregion

    //region PUBLIC METHODS - GRPC ENDPOINTS

    /**
     * Endpoint for making deposit requests.
     * Marked as synchronized to avoid multiple threads from operating over the same
     * transaction service doDeposit method at the same time, for example:
     *
     * T1 User1 deposit USD 100 - T2 User1 deposit USD 100 -> Transactional alone is not
     * preventing the creation of 2 entries in the balance table if the user USD balance
     * has not been created before or may not add up the real balance from both transactions.
     *
     * Synchronized here is helping prevent some concurrency problematic scenarios where
     * using Transactional alone is not enough.
     * @param request
     * @param responseObserver
     */
    @Override
    public synchronized void deposit(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        LOGGER.debug("Request for DEPOSIT received");

        String result = transactionService.doDeposit(request.getUserId(), request.getAmount(), request.getCurrency());

        TransactionResponse response = TransactionResponse.newBuilder().setMessage(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Endpoint for making withdraw requests.
     * Marked as synchronized to avoid multiple threads from operating over the same
     * transaction service doWithdraw method at the same time, for example:
     *
     * T1 User1 withdraw USD 100 - T2 User1 withdraw USD 100 -> Transactional alone is not
     * preventing the retrieval of both withdrawals from the balance table of the user USD balance
     * which is not valid if the user doesn't have enough funds or will not reflect the correct
     * amount in the balance.
     *
     * Synchronized here is helping prevent some concurrency problematic scenarios where
     * using Transactional alone is not enough.
     * @param request
     * @param responseObserver
     */
    @Override
    public synchronized void withdraw(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        LOGGER.debug("Request for WITHDRAW received");

        String result = transactionService.doWithdraw(request.getUserId(), request.getAmount(), request.getCurrency());

        TransactionResponse response = TransactionResponse.newBuilder().setMessage(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Endpoint for making balance requests.
     * @param request
     * @param responseObserver
     */
    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        LOGGER.debug("Request for GET_BALANCE received");

        Map<String, Long> balances = transactionService.getBalance(request.getUserId());

        BalanceResponse response = BalanceResponse.newBuilder().putAllBalances(balances).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //endregion
}
