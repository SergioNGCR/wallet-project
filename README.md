Wallet-Project - SergioNGCR
========================================

Wallet-Project contains a java gRPC library project, the Wallet-Server project an app with 
gRPC endpoints for Users to deposit, withdraw and check their wallet balances and the 
Wallet-Client project an app that simulates Users making requests to the server.

Requirements
========================================

Java should be installed on the machine, at least java 1.8.0_212.

For the project to work on your machine you need Docker installed and running, as the 
project makes use of docker-compose for downloading and running the MySQL server and 
Adminer for DB administration, ports 33306 and 9898 should be available for these two
programs to run properly.

Port 59090 should be available as it is needed for the Wallet-Server application.

How to Run
========================================

### Basic setup and running DB:

1. Clone the repository to machine.
2. Open a Terminal and navigate to the project's root folder.
3. Execute docker-compose to download and start the MySQL instance. (use -d for detached)
    ```
    docker-compose up -d
    ```
4. If running detached, check docker ps for when MySQL is up and running.
5. You can additionally login to Adminer on http://localhost:9898.
   ```
   Username: root
   Password: test
   database: wallet
   ```
6. Once you can login to the database, you can proceed.

### Building projects:

1. Still on the project root folder.
2. Run `./gradlew build`.
3. This should compile the code for all 3 projects and run unit and integration tests.

### Run Wallet-Server:

1. Still on the project root folder.
2. Run `java -jar wallet-server/build/libs/wallet-server-0.0.1-SNAPSHOT.jar`
3. Server should start and when ready it should be listening on port 59090.

### Run Wallet-Client:

1. Open a new Terminal and navigate to the project's root folder.
2. Run the following command, replace `<users>` with the amount of users to simulate, `<threadPerUser>` with the number 
of threads allowed per user and `<roundsPerThread>` with the number of rounds each thread has to execute. 
    ```
    java -jar wallet-client/build/libs/wallet-client-0.0.1-SNAPSHOT.jar <users> <threadPerUser> <roundsPerThread>
    ```
3. Client application should start and simulate multiple users making requests to the Wallet-Server.
4. If you want to hide all the debug messages just run the same command with an additional option:
    ```
    java -jar wallet-client/build/libs/wallet-client-0.0.1-SNAPSHOT.jar <users> <threadPerUser> <roundsPerThread> --logging.level.com=INFO
    ```

Important Project Choices
========================================

...

Performance Estimations
========================================

The whole solution was thoroughly tested doing unit, integration and stress testing, 
you can find in the file `test_results.txt` information about some of the stress tests 
done and the results.

My final estimations are based on the results from that file, the Wallet-Server is able 
to process around 76 requests per second under heavy loads, this makes it likely
capable of serving up to 6.6 million requests in 24 hours, meaning that if an average of 6.6 
requests per User is taken into account (based on the Rounds from the Wallet-Client app)
the Wallet-Server could serve up to 1 million users daily.