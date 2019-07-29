package com.sergio.wallet.client.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Simple factory like class in charge of providing callable tasks to make requests to the wallet-server.
 */
public abstract class RoundsFactory {

    //region PRIVATE METHODS

    /**
     * Method used to create a round using the roundToBuild parameter to choose from Rounds A, B and C.
     * User id and its logger are used to properly link the tasks to the user who owns it.
     * @param roundToBuild
     * @param user
     * @return A Callable task that will perform a set of grpc requests to the wallet-server.
     */
    private static Callable<Boolean> instantiateRound(int roundToBuild, final User user) {
        Callable<Boolean> callable;
        final String userIdString = String.valueOf(user.getId());

        switch (roundToBuild) {
            case 0:
                // Round A.
                callable = () -> {
                    String roundName = "Round A";
                    String threadName = Thread.currentThread().getName();

                    try {
                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName + " starting.");

                        String result = user.getWalletClient().deposit(userIdString, 100, "USD");

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " deposit result: " + result);

                        Map<String, Long> map = user.getWalletClient().getBalance(userIdString);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " balance USD: " + map.get("USD").intValue());

                        result = user.getWalletClient().withdraw(userIdString, 200, "USD");

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " withdraw result: " + result);

                        map = user.getWalletClient().getBalance(userIdString);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " balance USD: " + map.get("USD").intValue());

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName + " finished.");

                        return true;
                    } catch (Exception e) {
                        user.logError("User: " + userIdString + " | " + threadName + " | " + roundName + " An exception was thrown: "
                                + e.getMessage() + " | " + e.getStackTrace()[0].toString());
                        return false;
                    }
                };
                break;
            case 1:
                // Round B.
                callable = () -> {
                    TimeUnit.SECONDS.sleep(1);
                    String roundName = "Round B";
                    String name = Thread.currentThread().getName();
                    user.logInfo("User: " + userIdString + " | " + name + " | " + roundName + " Starting work.");
                    TimeUnit.SECONDS.sleep(2);
                    user.logInfo("User: " + userIdString + " | " + name + " | " + roundName  + " Work finished.");
                    return true;
                };
                break;
            case 2:
                // Round C.
                callable = () -> {
                    TimeUnit.SECONDS.sleep(1);
                    String roundName = "Round C";
                    String name = Thread.currentThread().getName();
                    user.logInfo("User: " + userIdString + " | " + name + " | " + roundName + " Starting work.");
                    TimeUnit.SECONDS.sleep(2);
                    user.logInfo("User: " + userIdString + " | " + name + " | " + roundName  + " Work finished.");
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
     * Simple method to create a specific number of tasks/Rounds in random order.
     * @param amount
     * @param user
     * @return A randomly created list of rounds for the users to execute.
     */
    public static List<Callable<Boolean>> getRoundsRandomly(int amount, final User user) {
        List<Callable<Boolean>> tmpRounds = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            //tmpRounds.add(instantiateRound(randomNumber(), user));
            tmpRounds.add(instantiateRound(0, user));
        }

        return Collections.unmodifiableList(tmpRounds);
    }

    //endregion

}
