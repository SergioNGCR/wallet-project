package com.sergio.wallet.client.simulation;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple factory like class in charge of providing callable tasks to make requests to the wallet-server.
 */
public abstract class RoundsFactory {

    //region PRIVATE METHODS

    /**
     * Method used to create a round using the roundToBuild parameter to choose from Rounds A, B and C.
     * User id and its logger are used to properly link the tasks to the user who owns it.
     *
     * This method should be refactored into multiple smaller methods per Round - not enough time.
     * @param roundToBuild
     * @param user
     * @return A Callable task that will perform a set of grpc requests to the wallet-server.
     */
    private static Callable<Boolean> instantiateRound(int roundToBuild, final User user) {
        Callable<Boolean> callable;
        final String userIdString = String.valueOf(user.getId());
        final String usd = "USD";
        final String eur = "EUR";
        final String gbp = "GBP";


        switch (roundToBuild) {
            case 0:
                // Round A.
                callable = () -> {
                    String roundName = "Round A";
                    String threadName = Thread.currentThread().getName();

                    try {
                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName + " starting.");

                        String result = user.getWalletClient().deposit(userIdString, 100, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Deposit " + 100 + " " + usd + " result: " + result);

                        result = user.getWalletClient().withdraw(userIdString, 200, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 200 + " " + usd + " result: " + result);

                        result = user.getWalletClient().deposit(userIdString, 100, eur);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Deposit " + 100 + " " + eur + " result: " + result);

                        Map<String, Long> map = user.getWalletClient().getBalance(userIdString);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " balance - "
                                + (map.containsKey(usd) ? usd + " : " + map.get(usd).intValue() + " - " : " - ")
                                + (map.containsKey(eur) ? eur + " : " + map.get(eur).intValue() + " - " : " - ")
                                + (map.containsKey(gbp) ? gbp + " : " + map.get(gbp).intValue() : ""));

                        result = user.getWalletClient().withdraw(userIdString, 100, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 100 + " " + usd + " result: " + result);

                        map = user.getWalletClient().getBalance(userIdString);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " balance - "
                                + (map.containsKey(usd) ? usd + " : " + map.get(usd).intValue() + " - " : " - ")
                                + (map.containsKey(eur) ? eur + " : " + map.get(eur).intValue() + " - " : " - ")
                                + (map.containsKey(gbp) ? gbp + " : " + map.get(gbp).intValue() : ""));

                        result = user.getWalletClient().withdraw(userIdString, 100, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 100 + " " + usd + " result: " + result);

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
                    String roundName = "Round B";
                    String threadName = Thread.currentThread().getName();

                    try {
                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName + " starting.");

                        String result = user.getWalletClient().withdraw(userIdString, 100, gbp);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 100 + " " + gbp + " result: " + result);

                        result = user.getWalletClient().deposit(userIdString, 300, "GPB");

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Deposit " + 300 + " " + "GPB" + " result: " + result);

                        result = user.getWalletClient().withdraw(userIdString, 100, gbp);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 100 + " " + gbp + " result: " + result);

                        result = user.getWalletClient().withdraw(userIdString, 100, gbp);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 100 + " " + gbp + " result: " + result);

                        result = user.getWalletClient().withdraw(userIdString, 100, gbp);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 100 + " " + gbp + " result: " + result);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName + " finished.");

                        return true;
                    } catch (Exception e) {
                        user.logError("User: " + userIdString + " | " + threadName + " | " + roundName + " An exception was thrown: "
                                + e.getMessage() + " | " + e.getStackTrace()[0].toString());
                        return false;
                    }
                };
                break;
            case 2:
                // Round C.
                callable = () -> {
                    String roundName = "Round C";
                    String threadName = Thread.currentThread().getName();

                    try {
                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName + " starting.");

                        Map<String, Long> map = user.getWalletClient().getBalance(userIdString);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " balance - "
                                + (map.containsKey(usd) ? usd + " : " + map.get(usd).intValue() + " - " : " - ")
                                + (map.containsKey(eur) ? eur + " : " + map.get(eur).intValue() + " - " : " - ")
                                + (map.containsKey(gbp) ? gbp + " : " + map.get(gbp).intValue() : ""));

                        String result = user.getWalletClient().deposit(userIdString, 100, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Deposit " + 100 + " " + usd + " result: " + result);

                        result = user.getWalletClient().deposit(userIdString, 100, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Deposit " + 100 + " " + usd + " result: " + result);

                        result = user.getWalletClient().withdraw(userIdString, 100, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 100 + " " + usd + " result: " + result);

                        result = user.getWalletClient().deposit(userIdString, 100, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Deposit " + 100 + " " + usd + " result: " + result);

                        map = user.getWalletClient().getBalance(userIdString);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " balance - "
                                + (map.containsKey(usd) ? usd + " : " + map.get(usd).intValue() + " - " : " - ")
                                + (map.containsKey(eur) ? eur + " : " + map.get(eur).intValue() + " - " : " - ")
                                + (map.containsKey(gbp) ? gbp + " : " + map.get(gbp).intValue() : ""));

                        result = user.getWalletClient().withdraw(userIdString, 200, usd);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " Withdraw " + 200 + " " + usd + " result: " + result);

                        map = user.getWalletClient().getBalance(userIdString);

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName
                                + " balance - "
                                + (map.containsKey(usd) ? usd + " : " + map.get(usd).intValue() + " - " : " - ")
                                + (map.containsKey(eur) ? eur + " : " + map.get(eur).intValue() + " - " : " - ")
                                + (map.containsKey(gbp) ? gbp + " : " + map.get(gbp).intValue() : ""));

                        user.logDebug("User: " + userIdString + " | " + threadName + " | " + roundName + " finished.");

                        return true;
                    } catch (Exception e) {
                        user.logError("User: " + userIdString + " | " + threadName + " | " + roundName + " An exception was thrown: "
                                + e.getMessage() + " | " + e.getStackTrace()[0].toString());
                        return false;
                    }
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
            tmpRounds.add(instantiateRound(randomNumber(), user));
        }

        return Collections.unmodifiableList(tmpRounds);
    }

    //endregion

}
