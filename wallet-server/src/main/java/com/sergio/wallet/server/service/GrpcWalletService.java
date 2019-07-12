package com.sergio.wallet.server.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sergio.grpc.HelloRequest;
import org.sergio.grpc.HelloResponse;
import org.sergio.grpc.HelloServiceGrpc;

@GrpcService
public class GrpcWalletService extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void hello(HelloRequest req, StreamObserver<HelloResponse> responseStreamObserver) {
        HelloResponse reply = HelloResponse.newBuilder().setGreeting("Hello ==> " + req.getFirstName()).build();
        responseStreamObserver.onNext(reply);
        responseStreamObserver.onCompleted();
    }

}
