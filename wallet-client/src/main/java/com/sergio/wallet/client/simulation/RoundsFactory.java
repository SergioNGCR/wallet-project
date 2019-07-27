package com.sergio.wallet.client.simulation;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Simple factory like class in charge of providing callable tasks to make requests to the wallet-server.
 */
public abstract class RoundsFactory {

    //region PRIVATE METHODS

    /**
     *
     * @param roundToBuild
     * @param userId
     * @param userLogger
     * @return
     */
    private static Callable<Boolean> instantiateRound(int roundToBuild, int userId, Logger userLogger) {
        Callable<Boolean> callable;

        switch (roundToBuild) {
            case 0:
                // Round A.
                callable = () -> {
                    TimeUnit.SECONDS.sleep(1);
                    String roundName = "Round A";
                    String name = Thread.currentThread().getName();
                    userLogger.info("User: " + userId + " | " + name + " | " + roundName + " Starting work.");
                    TimeUnit.SECONDS.sleep(2);
                    userLogger.info("User: " + userId + " | " + name + " | " + roundName  + " Work finished.");
                    return true;
                };
                break;
            case 1:
                // Round B.
                callable = () -> {
                    TimeUnit.SECONDS.sleep(1);
                    String roundName = "Round B";
                    String name = Thread.currentThread().getName();
                    userLogger.info("User: " + userId + " | " + name + " | " + roundName + " Starting work.");
                    TimeUnit.SECONDS.sleep(2);
                    userLogger.info("User: " + userId + " | " + name + " | " + roundName  + " Work finished.");
                    return true;
                };
                break;
            case 2:
                // Round C.
                callable = () -> {
                    TimeUnit.SECONDS.sleep(1);
                    String roundName = "Round C";
                    String name = Thread.currentThread().getName();
                    userLogger.info("User: " + userId + " | " + name + " | " + roundName + " Starting work.");
                    TimeUnit.SECONDS.sleep(2);
                    userLogger.info("User: " + userId + " | " + name + " | " + roundName  + " Work finished.");
                    return true;
                };
                break;
            default:
                callable = () -> false;
                break;
        }

        return callable;
    }

    /** Simple random number generator. */
    private static int randomNumber() {
        int min = 0;
        int max = 2;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    //endregion

    //region PUBLIC METHODS

    /**
     *
     * @param amount
     * @param userId
     * @param userLogger
     * @return
     */
    public static List<Callable<Boolean>> getRoundsRandomly(int amount, int userId, Logger userLogger) {
        List<Callable<Boolean>> tmpRounds = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            tmpRounds.add(instantiateRound(randomNumber(), userId, userLogger));
        }

        return tmpRounds;
    }

    //endregion

}
