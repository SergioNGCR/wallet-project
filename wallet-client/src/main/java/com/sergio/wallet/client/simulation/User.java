package com.sergio.wallet.client.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

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

    //endregion

    //region CONSTRUCTORS

    public User(int id, int maxAllowedThreads, int roundsPerThread) {
        this.id = id;

        // Define and execute some sort of user simulation.
        List<ExecutorService> tmpExecutors = new ArrayList<>();

        for (int i = 1; i <= maxAllowedThreads; i++) {
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

        // Use external class to pick from the available rounds at random.
        // This will fill the list of callable tasks to be done by each thread pool.

        // Test tasks.
        for (int i = 0; i < this.threadPools.size(); i++) {
            List<Callable<Boolean>> callables = new ArrayList<>();
            for (int j = 0; j < roundsPerThread; j++) {
                final int callId = j+1;
                callables.add(() -> {
                    TimeUnit.SECONDS.sleep(1);
                    String roundName = "Round"+callId;
                    String name = Thread.currentThread().getName();
                    LOGGER.info("User: " + this.id + " | " + name + " | " + roundName + " Starting work.");
                    TimeUnit.SECONDS.sleep(2);
                    LOGGER.info("User: " + this.id + " | " + name + " | " + roundName  + " Work finished.");
                    return true;
                });
            }
            this.callableTasksMap.put(i, callables);
        }
    }

    //endregion

    //region PUBLIC METHODS

    public int getId() {
        return id;
    }

    /**
     * Simple method to validate if the user is running any tasks.
     * @return false if it hasn't started yet or there are any tasks not finished yet, true otherwise.
     */
    public boolean isRunningTasks() {
        // We want to quickly report that there are tasks still running.
        // Object boolean as we will receive null when all tasks are done.
        Boolean anyPendingTask = this.futureTasksMap.searchValues(1, futures ->
                    futures.parallelStream().anyMatch(future -> !future.isDone())
                );

        return anyPendingTask == null ? false : anyPendingTask.booleanValue();
    }

    /**
     * Start doing the requests to the wallet-server.
     */
    public void executeTasks() {
        int idx = 0;

        for (ExecutorService executor : this.threadPools) {
            List<Future<Boolean>> futures = new ArrayList<>();
            for (Callable<Boolean> task : this.callableTasksMap.get(idx)){
                futures.add(executor.submit(task));
            }
            this.futureTasksMap.put(idx, futures);
            idx++;
        }

        LOGGER.info("User: " + this.getId() + " started execution on all threads!!");
    }

    /**
     * Make sure to shutdown all thread pools/executors used by this user.
     */
    public void shutdownUser() {
        this.threadPools.parallelStream().forEach(executor -> {
            try {
                LOGGER.info("User: " + this.getId() + " | " + "attempting to shutdown executor");
                executor.shutdown();
                executor.awaitTermination(20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.info("User: " + this.getId() + " | " + "tasks interrupted");
            } finally {
                if (!executor.isTerminated()) {
                    LOGGER.info("User: " + this.getId() + " | " + "cancel non-finished tasks");
                }
                executor.shutdownNow();
                LOGGER.info("User: " + this.getId() + " | " + "shutdown finished");
            }
        });
    }

    //endregion

}
