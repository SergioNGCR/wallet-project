package com.sergio.wallet.server.test.grpc;

import com.sergio.wallet.server.test.service.TransactionService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.sergio.wallet.grpc.*;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceImplBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
     * @param request
     * @param responseObserver
     */
    @Override
    public void deposit(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        LOGGER.debug("Request for DEPOSIT received");

        String result;

        if(transactionService.isValidCurrency(request.getCurrency())) {
             result = transactionService.doDeposit(request.getUserId(), request.getAmount(), request.getCurrency());
        } else {
            result = TransactionService.RESPONSE_UNKNOWN_CURRENCY;
        }

        TransactionResponse response = TransactionResponse.newBuilder().setMessage(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Endpoint for making withdraw requests.
     * @param request
     * @param responseObserver
     */
    @Override
    public void withdraw(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        LOGGER.debug("Request for WITHDRAW received");

        String result;

        if(transactionService.isValidCurrency(request.getCurrency())) {
            result = transactionService.doWithdraw(request.getUserId(), request.getAmount(), request.getCurrency());
        } else {
            result = TransactionService.RESPONSE_UNKNOWN_CURRENCY;
        }

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
