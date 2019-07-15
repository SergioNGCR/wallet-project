package com.sergio.wallet.client.Components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.sergio.wallet.client.service.GrpcHelloService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class RunnerComponent implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(RunnerComponent.class);

    private final GrpcHelloService helloService;

    @Autowired
    public RunnerComponent(GrpcHelloService helloService){
        super();
        this.helloService = helloService;
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

        String greeting = helloService.sendHelloMessage(firstName, lastName);

        LOGGER.info("Recieved: " + greeting);
    }
}
