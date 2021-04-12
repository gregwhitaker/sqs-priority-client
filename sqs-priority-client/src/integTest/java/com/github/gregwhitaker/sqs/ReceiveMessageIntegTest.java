package com.github.gregwhitaker.sqs;

import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class ReceiveMessageIntegTest {
  private static final String SQS_ENDPOINT = "http://localhost:4566";

  public static void main(String... args) throws Exception {
    final SqsClient sqs = SqsClient.builder()
            .endpointOverride(URI.create(SQS_ENDPOINT))
            .build();

    final SqsPriorityClient sqsPriorityClient = SqsPriorityClient.builder(sqs)
            .withQueues()
              .queue("high-priority-queue", 0.80)
              .queue("medium-priority-queue", 0.15)
              .queue("low-priority-queue", 0.05)
            .end()
            .withMaxNumberOfMessages(10)
            .build();

    final CountDownLatch latch = new CountDownLatch(1);

    sqsPriorityClient.receiveMessages()
            .flatMap(message -> {
              System.out.printf("Message %s: %s%n", message.messageId(), message.body());
              return sqsPriorityClient.deleteMessage(message.receiptHandle());
            })
            .doOnComplete(latch::countDown)
            .subscribe();

    latch.await();
  }
}
