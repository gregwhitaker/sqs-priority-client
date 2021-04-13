# Example
Example of reading multiple Amazon SQS queues with weighted priorities using the sqs-priority-client.

![example-diagram](../diagram.png)

The example application reads 1,000 messages from each of three queues with differently configured priorities.

## Building the Example
Run the following command to build the example application:

    ./gradlew clean build

## Running the Example
Run the following command to start the example application:

    ./gradlew :example:run

  If successful, you will see messages being printed to the console:

    [boundedElastic-3] Message 56053564-dfbd-98a8-c51f-ffd99714d902: low-priority1
    [boundedElastic-1] Message 3889e52d-a712-5298-efd7-4e0e7254c24f: high-priority11
    [boundedElastic-2] Message a51432fe-bf15-4765-7687-3a6787f75c1d: high-priority1
    [boundedElastic-1] Message ab6b78ff-4a16-11ab-0a31-207bc5021c52: high-priority12
    [boundedElastic-3] Message 417de9c2-4c39-d37d-2343-3fa01e6ce4a7: low-priority2
    [boundedElastic-2] Message d52fb5c5-80da-52e0-879c-2dabe101d663: high-priority2
    [boundedElastic-3] Message 1ece719a-742a-2bc5-a135-0a6dfc295bd3: low-priority3
    [boundedElastic-1] Message dae9254f-f44a-cf54-9710-0db354b486a3: high-priority13
    [boundedElastic-2] Message 6087b1b1-46e5-dd57-04d2-a7502b25b983: high-priority3
    [boundedElastic-2] Message 784d2dfb-f8d1-38a8-de6c-0776f8fd5031: high-priority4
    [boundedElastic-3] Message 4fcf4337-1ec9-d7d4-e055-d7cb5661f963: low-priority4
    [boundedElastic-1] Message 4c45ebb3-522a-1059-e742-040fdde42247: high-priority14
    [boundedElastic-2] Message 70aea787-4e09-4cc9-8a5e-6cd450f30513: high-priority5