package com.sergio.wallet.client.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sergio.wallet.client.grpc.GrpcWalletClient;

import java.util.Map;


@Component
public class RunnerComponent implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(RunnerComponent.class);

    private final GrpcWalletClient walletService;

    @Autowired
    public RunnerComponent(GrpcWalletClient walletService){
        super();
        this.walletService = walletService;
    }

    @Override
    public void run(String... args) throws Exception {
        String firstName = "";
        String lastName = "";

        if(args.length >= 2) {
            firstName = args[0];
            lastName = args[1];
        }

        LOGGER.info("Sending request: firstName: " + firstName + " | lastname: " + lastName);

        String result = this.walletService.deposit(firstName, 100, "USD");

        LOGGER.info("Recieved: " + result);


        result = this.walletService.withdraw(firstName, 200, "USD");

        LOGGER.info("Recieved: " + result);


        Map<String, Long> map = this.walletService.getBalance(firstName);

        LOGGER.info("Recieved: Map size " + map.size());
    }
}
