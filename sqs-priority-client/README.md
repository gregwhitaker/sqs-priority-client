# sqs-priority-client
Reactive wrapper around the [Amazon SQS](https://aws.amazon.com/sqs/) client that allows reading from multiple queues with weighted priority.

## Documentation
For detailed documentation on this library and its features please refer to the [SQS Priority Client User Guide](https://gregwhitaker.github.io/sqs-priority-client/).

## Build
Run the following command to build the library:

    ./gradlew clean build

## Release
Follow the steps below to create a new release of the library:

1. Increment the version number in the [root build](../build.gradle).
2. Create a new release via [GitHub Releases](https://github.com/gregwhitaker/sqs-priority-client/releases).

Once the release has been created, a GitHub Action will automatically be invoked to publish the release to the GitHub Package Repository.