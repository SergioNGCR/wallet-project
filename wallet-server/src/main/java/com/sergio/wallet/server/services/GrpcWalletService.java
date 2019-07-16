package com.sergio.wallet.server.services;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sergio.wallet.grpc.*;

import java.util.Arrays;
import java.util.List;

@GrpcService
public class GrpcWalletService extends WalletServiceGrpc.WalletServiceImplBase {

    private final static List<String> VALID_CURRENCIES = Arrays.asList("USD", "EUR", "GBP");

    private String ExecuteTransaction(String userId, int amount, String currency, boolean isDeposit) {
        if (!VALID_CURRENCIES.contains(currency)) {
            return "Unknown currency";
        } else {
            // Some transaction functionality based on the type.

            // Empty response equals to successful transaction.
            return "";
        }
    }

    /*@Override
    public void hello(HelloRequest req, StreamObserver<HelloResponse> responseStreamObserver) {
        HelloResponse reply = HelloResponse.newBuilder().setGreeting("Hello ==> " + req.getFirstName()).build();
        responseStreamObserver.onNext(reply);
        responseStreamObserver.onCompleted();
    }*/

    @Override
    public void deposit(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        String result = ExecuteTransaction(request.getUserId(), request.getAmount(), request.getCurrency(), true);

        TransactionResponse response = TransactionResponse.newBuilder().setMessage(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void withdraw(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        String result = ExecuteTransaction(request.getUserId(), request.getAmount(), request.getCurrency(), false);

        TransactionResponse response = TransactionResponse.newBuilder().setMessage(result).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        BalanceResponse response = BalanceResponse.newBuilder().putBalances("test", 0).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
