package com.sergio.wallet.client.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.sergio.wallet.client.grpc.GrpcWalletClient;

import java.util.*;
import java.util.concurrent.*;

/**
 * Simple class to emulate the idea of a user that will perform tasks/requests to the wallet-server.
 */
public class User {

    //region VARIABLES

    private final static Logger LOGGER = LoggerFactory.getLogger(User.class);

    /**
     * Will hold the list of executors that will do the threading work for the user.
     * It will be instantiated with an immutable list to be safe for multithreading.
     */
    private final List<ExecutorService> threadPools;

    /** Will hold the lists of callable tasks for each thread pool to execute. */
    private Map<Integer, List<Callable<Boolean>>> callableTasksMap;

    /** Will hold the lists of future tasks for each thread pool to validate for completion. */
    private ConcurrentHashMap<Integer, List<Future<Boolean>>> futureTasksMap;

    private final int id;

    /** A GrpcWalletClient instance exclusively for each user. */
    private final GrpcWalletClient walletClient;

    //endregion

    //region CONSTRUCTORS

    /**
     * User constructor to assign it an id, the number of allowed threads to use for task execution and the number
     * of rounds each thread needs to complete.
     * @param id
     * @param maxAllowedThreads
     * @param roundsPerThread
     */
    public User(int id, int maxAllowedThreads, int roundsPerThread, GrpcWalletClient walletClient) {
        this.id = id;

        this.walletClient = walletClient;

        // Define and execute some sort of user simulation.
        List<ExecutorService> tmpExecutors = new ArrayList<>();

        for (int i = 1; i <= maxAllowedThreads; i++) {
            // Create a new single thread Executor so that only 1 thread is processing the tasks in the queue.
            tmpExecutors.add(Executors.newSingleThreadExecutor(
                    new CustomizableThreadFactory("user-" + this.getId() + "-pool-" + i + "-thread-")));
        }

        this.threadPools = Collections.unmodifiableList(tmpExecutors);

        pickRounds(roundsPerThread);

        this.futureTasksMap = new ConcurrentHashMap<>();
    }

    //endregion

    //region PRIVATE METHODS

    /**
     * Method used for randomly picking rounds to execute for each thread.
     * @param roundsPerThread Number of rounds for each thread to execute.
     */
    private void pickRounds(int roundsPerThread) {
        this.callableTasksMap = new HashMap<>();

        // Use the rounds factory class to pick from the available rounds at random.
        for (int i = 0; i < this.threadPools.size(); i++) {
            this.callableTasksMap.put(i, RoundsFactory.getRoundsRandomly(roundsPerThread, this));
        }
    }

    //endregion

    //region PUBLIC METHODS

    public int getId() {
        return this.id;
    }

    public GrpcWalletClient getWalletClient() {
        return this.walletClient;
    }

    /**
     * Simple method to log info messages using the User class logger.
     * @param message
     */
    public void logInfo(String message) {
        LOGGER.info(message);
    }

    /**
     * Simple method to log error messages using the User class logger.
     * @param message
     */
    public void logError(String message) {
        LOGGER.error(message);
    }

    /**
     * Simple method to log debug messages using the User class logger.
     * @param message
     */
    public void logDebug(String message) {
        LOGGER.debug(message);
    }

    /**
     * Simple method to validate if the user is running any tasks.
     * @return false if it hasn't started yet or there are any tasks not finished yet, true otherwise.
     */
    public boolean isRunningTasks() {
        // We want to quickly report that there are tasks still running.
        // Object boolean as we will receive null when all tasks are done.
        Boolean anyPendingTask = this.futureTasksMap.values().stream().anyMatch(futures ->
                    futures.stream().anyMatch(future -> !future.isDone())
                );

        return anyPendingTask == null ? false : anyPendingTask.booleanValue();
    }

    /**
     * Start doing the requests to the wallet-server.
     */
    public void executeTasks() {
        int idx = 0;

        try {
            for (ExecutorService executor : this.threadPools) {
                List<Future<Boolean>> futures = new ArrayList<>();
                for (Callable<Boolean> task : this.callableTasksMap.get(idx)) {
                    futures.add(executor.submit(task));
                }
                this.futureTasksMap.put(idx, futures);
                idx++;
            }

            logInfo("User: " + this.getId() + " started execution on all threads.");
        } catch (Exception e) {
            logError("User: " + this.getId() + " | error thrown while starting execution | " + e.getMessage());
        }
    }

    /**
     * Make sure to shutdown all thread pools/executors used by this user.
     */
    public void shutdownUser() {
        this.threadPools.parallelStream().forEach(executor -> {
            try {
                logDebug("User: " + this.getId() + " | " + "attempting to shutdown executor.");
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logDebug("User: " + this.getId() + " | " + "shutdown interrupted.");
            } finally {
                if (!executor.isTerminated()) {
                    logDebug("User: " + this.getId() + " | " + "cancel non-finished tasks.");
                }
                executor.shutdownNow();
                logDebug("User: " + this.getId() + " | " + "shutdown finished.");
            }
        });
    }

    //endregion

}
