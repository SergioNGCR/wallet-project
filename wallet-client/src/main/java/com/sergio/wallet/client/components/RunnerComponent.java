package com.sergio.wallet.client.components;

import com.sergio.wallet.client.simulation.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for the Client app, this Command Line Runner will receive the parameters passed by the user
 * and begin a simulation of users performing requests to the Wallet server application via gRPC calls.
 */
@Component
public class RunnerComponent implements CommandLineRunner {

    //region VARIABLES

    private final static Logger LOGGER = LoggerFactory.getLogger(RunnerComponent.class);

    // Necessary for defining at configuration level the arguments pass to the application.
    private final WalletClientConfiguration walletClientConfiguration;

    // Necessary for creating User beans that have their own GrpcWalletClient instance.
    private final ApplicationContext applicationContext;

    // Used for simple memory status reports for debug logging.
    private final Runtime runtime = Runtime.getRuntime();

    // Keeping track of how many seconds have past since last print of memory status.
    private long lastMemPrint;

    //endregion

    //region CONSTRUCTORS

    @Autowired
    public RunnerComponent(ApplicationContext applicationContext, WalletClientConfiguration walletClientConfiguration) {
        super();
        this.applicationContext = applicationContext;
        this.walletClientConfiguration = walletClientConfiguration;
    }

    //endregion

    //region PUBLIC METHODS

    /**
     * Simple method to print the memory status.
     */
    public void printMemoryStatus() {
        LOGGER.debug(String.format("Memory Status | Free: %d MB, Total: %d MB, Max: %d MB",
                (runtime.freeMemory() / 1024) / 1024,
                (runtime.totalMemory() / 1024) / 1024,
                (runtime.maxMemory() / 1024) / 1024));

        lastMemPrint = Instant.now().getEpochSecond();
    }

    /**
     * Entry point for the client app, this method receives the parameters and starts the user simulation.
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

            // Set the parameters at application's configuration level, necessary for User beans creation.
            this.walletClientConfiguration.setThreadsPerUser(threadsPerUser);
            this.walletClientConfiguration.setRoundsPerThread(roundsPerThread);

            LOGGER.info("Users: " + users + " | " + "Threads: " + threadsPerUser + " | " + "Rounds: " + roundsPerThread);
        }

        // Only run the simulation if amount higher than 0, otherwise newFixedThreadPool throws exception.
        if (users <= 0 || users > 250) {
            if (users >= 250) {
                LOGGER.error("Current known limitation, with more than 250 users the gRPC client will start "
                        + "throwing errors like \"UNAVAILABLE: Channel shutdown invoked\" "
                        + "please test with 250 users or less.");
            } else {
                LOGGER.error("Simulation can only be run if users is higher than 0.");
            }
            return;
        }

        // Start the simulation of users.
        List<User> simulatedUsers = new ArrayList<>();

        // Use the application context for creating User beans.
        for (int i = 0; i < users; i++) {
            User user = applicationContext.getBean(User.class);
            simulatedUsers.add(user);
        }

        // Start in parallel the execution of all the user's tasks.
        simulatedUsers.parallelStream().forEach(user -> user.executeTasks());

        LOGGER.info("Client simulation started.");

        // Simple validation to wait for users to complete all tasks.
        boolean workFinished = false;
        try {
            printMemoryStatus();
            while (!workFinished) {
                //LOGGER.info("WAITING - 50 ms");
                workFinished = simulatedUsers.parallelStream().allMatch(user -> !user.isRunningTasks());
                if (!workFinished) TimeUnit.MILLISECONDS.sleep(50);
                if (Instant.now().getEpochSecond() - lastMemPrint >= 5) printMemoryStatus();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Simulation failed | " + e.getMessage());
        }

        LOGGER.info("Client simulation finished.");

        // Start in parallel the shutdown of all the user's executors.
        simulatedUsers.parallelStream().forEach(user -> user.shutdownUser());

        LOGGER.info("Client application finished shutdown of all executors.");
    }

    //endregion

}
