package com.sergio.wallet.server.service;

import com.sergio.wallet.server.data.repository.TransactionRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.sergio.wallet.grpc.*;
import org.sergio.wallet.grpc.WalletServiceGrpc.WalletServiceImplBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/***
 * Main gRPC Service class that handles the endpoints for the wallet server, it provides the functionality
 * for making deposits, withdraws and retrieving an user's balance for all currencies.
 */
@GrpcService
public class GrpcWalletService extends WalletServiceImplBase {

    private final static Logger LOGGER = LoggerFactory.getLogger(GrpcWalletService.class);

    private final static List<String> VALID_CURRENCIES = Arrays.asList("USD", "EUR", "GBP");

    public final static String RESPONSE_SUCCESSFUL = "";

    public final static String RESPONSE_UNKNOWN_CURRENCY = "Unknown currency";

    public final static String RESPONSE_INSUFFICIENT_FUNDS = "Insufficient funds";

    private final TransactionRepository transactionRepository;

    @Autowired
    public GrpcWalletService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /***
     * Method that will handle which repository methods need to be called depending on the request.
     * @param userId
     * @param amount
     * @param currency
     * @param isDeposit
     * @return Empty String if successful or the error message.
     */
    private String ExecuteTransaction(String userId, int amount, String currency, boolean isDeposit) {
        if (!VALID_CURRENCIES.contains(currency)) {
            return RESPONSE_UNKNOWN_CURRENCY;
        } else {
            // Some transaction functionality based on the type.


            // Empty response equals to successful transaction.
            return RESPONSE_SUCCESSFUL;
        }
    }

    /***
     * Endpoint for making deposit requests.
     * @param request
     * @param responseObserver
     */
    @Override
    public void deposit(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        LOGGER.debug("Request for DEPOSIT received");

        String result = ExecuteTransaction(request.getUserId(), request.getAmount(), request.getCurrency(), true);

        TransactionResponse response = TransactionResponse.newBuilder().setMessage(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /***
     * Endpoint for making withdraw requests.
     * @param request
     * @param responseObserver
     */
    @Override
    public void withdraw(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        LOGGER.debug("Request for WITHDRAW received");

        String result = ExecuteTransaction(request.getUserId(), request.getAmount(), request.getCurrency(), false);

        TransactionResponse response = TransactionResponse.newBuilder().setMessage(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /***
     * Endpoint for making balance requests.
     * @param request
     * @param responseObserver
     */
    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        LOGGER.debug("Request for GET_BALANCE received");

        BalanceResponse response = BalanceResponse.newBuilder().putBalances(VALID_CURRENCIES.get(0), 0).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
