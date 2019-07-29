package com.sergio.wallet.client.components;

import com.sergio.wallet.client.grpc.GrpcWalletClient;
import com.sergio.wallet.client.simulation.User;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Main application configuration class, needed for the proper creation of User beans.
 * This is done so that each User bean can have an individual (prototype scoped)
 * GrpcWalletClient instance and so avoiding possible bottlenecks by using all the same
 * application wide instance, additionally this helps avoiding the need of passing along
 * many times a single GrpcWalletClient instance for all Users.
 * It also holds some of the arguments passed to the application for proper User creation.
 */
@Configuration
public class WalletClientConfiguration {

    //region VARIABLES

    // Holds the value for the next user's id.
    private int nextUserId = 1;

    // Holds the number of allowed threads per User.
    private int threadsPerUser = 1;

    // Holds the number of rounds each user thread has to execute.
    private int roundsPerThread = 1;

    //endregion

    //region PUBLIC METHODS

    public int getThreadsPerUser() {
        return threadsPerUser;
    }

    public void setThreadsPerUser(int threadsPerUser) {
        this.threadsPerUser = threadsPerUser;
    }

    public int getRoundsPerThread() {
        return roundsPerThread;
    }

    public void setRoundsPerThread(int roundsPerThread) {
        this.roundsPerThread = roundsPerThread;
    }

    //region BEAN DEFINITIONS

    /**
     *
     * Bean definition needed for creating individual Users with their own GrpcWalletClient instance.
     * @param walletClient An individual (prototype scoped) GrpcWalletClient instance to assign to each User.
     * @return An user instance with all the necessary information and it's own GrpcWalletClient.
     */
    @Bean()
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public User user(GrpcWalletClient walletClient) {
        User user = new User(nextUserId, threadsPerUser, roundsPerThread, walletClient);
        nextUserId++;
        return user;
    }

    //endregion

    //endregion
}
