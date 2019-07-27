package com.sergio.wallet.client.components;

import com.sergio.wallet.client.simulation.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sergio.wallet.client.grpc.GrpcWalletClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for the Client app, this Command Line Runner will receive the parameters passed by the user
 * and begin a simulation of users performing requests to the Wallet server application via gRPC calls.
 */
@Component
public class RunnerComponent implements CommandLineRunner {

    //region VARIABLES

    private final static Logger LOGGER = LoggerFactory.getLogger(RunnerComponent.class);

    private final boolean testingGrpc = false;

    private final GrpcWalletClient walletService;

    //endregion

    //region CONSTRUCTORS

    @Autowired
    public RunnerComponent(GrpcWalletClient walletService) {
        super();
        this.walletService = walletService;
    }

    //endregion

    //region PUBLIC METHODS

    /**
     * Entry point for the client app, this method receives the parameters and starts the user simulation.
     *
     * @param args Parameters send by the user when running the app.
     */
    @Override
    public void run(String... args) {
        int users = 0;
        int threadsPerUser = 1;
        int roundsPerThread = 1;

        if (args.length >= 3) {
            users = Integer.parseInt(args[0]);
            threadsPerUser = Integer.parseInt(args[1]);
            roundsPerThread = Integer.parseInt(args[2]);

            LOGGER.info("Users: " + users + " | " + "Threads: " + threadsPerUser + " | " + "Rounds: " + roundsPerThread);
        }

        if (this.testingGrpc) {
            String user = "sergio";

            String result = this.walletService.deposit(user, 100, "USD");

            LOGGER.info("Recieved: " + result);


            result = this.walletService.withdraw(user, 200, "USD");

            LOGGER.info("Recieved: " + result);


            Map<String, Long> map = this.walletService.getBalance(user);

            LOGGER.info("Recieved: Map size " + map.size());
        }

        // Only run the simulation if amount higher than 0, otherwise newFixedThreadPool throws exception.
        if (users <= 0) {
            LOGGER.info("Simulation can only be run if users is higher than 0.");
            return;
        }

        // Define and execute some sort of user simulation.
        List<User> simulatedUsers = new ArrayList<>();

        for (int i = 0; i < users; i++) {
            User user = new User(i+1, threadsPerUser, roundsPerThread);
            simulatedUsers.add(user);
        }

        simulatedUsers.parallelStream().forEach(user -> user.executeTasks());

        LOGGER.info("WORK STARTED");

        boolean workFinished = false;
        try {
            while (!workFinished) {
                LOGGER.info("WAITING for 1 second while users finish work.");
                workFinished = simulatedUsers.parallelStream().allMatch(user -> !user.isRunningTasks());
                if (!workFinished) TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            LOGGER.info("Simulation failed | " + e.getMessage());
        }

        LOGGER.info("WORK FINISHED");

        simulatedUsers.parallelStream().forEach(user -> user.shutdownUser());

        LOGGER.info("PROGRAM GOT TO END");
    }

    //endregion

}
