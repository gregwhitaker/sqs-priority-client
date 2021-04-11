package com.github.gregwhitaker.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SQS client that receives messages from multiple queues based on weighted priority.
 */
public class SqsPriorityClient {
  private static final Logger LOG = LoggerFactory.getLogger(SqsPriorityClient.class);
  private static final Random RAND = new Random(System.currentTimeMillis());

  private final SqsPriorityClientConfig config;
  private List<Double> queueThresholds;
  private List<String> queueUrls;

  SqsPriorityClient(SqsPriorityClientConfig config) {
    this.config = config;
    initQueues(config);
  }

  /**
   * Gets the builder for constructing an instance of {@link SqsPriorityClient}.
   *
   * @param sqs
   * @return
   */
  public static SqsPriorityClientBuilder builder(final SqsClient sqs) {
    return new SqsPriorityClientBuilder(sqs);
  }

  /**
   * Receives a stream of messages that never completes.
   *
   * @return a {@link Flux<Message>}
   */
  public Flux<Message> receiveMessages() {
    return receiveMessages(Long.MAX_VALUE);
  }

  /**
   * Receives a finite stream of the specified number of messages and then completes.
   *
   * @param count number of messages to receive
   * @return a {@link Flux<Message>}
   */
  public Flux<Message> receiveMessages(final long count) {
    return Flux.create(sink -> {
      final AtomicLong rcvCnt = new AtomicLong();
      while (rcvCnt.get() <= count) {
        final ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(nextQueueUrl())
                .maxNumberOfMessages(config.getMaxNumberOfMessages())
                .build();

        final ReceiveMessageResponse receiveMessageResponse = config.getSqsClient().receiveMessage(request);
        if (receiveMessageResponse.hasMessages()) {
          receiveMessageResponse.messages().forEach(message -> {
            sink.next(message);
            rcvCnt.incrementAndGet();
          });
        }
      }

      sink.complete();
    });
  }

  /**
   *
   * @param receiptHandle
   * @return
   */
  public Mono<Void> deleteMessage(final String receiptHandle) {
    return null;
  }

  /**
   * Gets the next queue url to read.
   *
   * @return queue url
   */
  private String nextQueueUrl() {
    final double nextRand = RAND.nextDouble();

    for (int cnt = 0; cnt < queueThresholds.size(); cnt++) {
      if (nextRand >= queueThresholds.get(cnt)) {
        return queueUrls.get(cnt);
      }
    }

    // Return highest priority if no other matches found
    return queueUrls.get(queueUrls.size() - 1);
  }

  /**
   * Initializes the queue threshold and queue url mappings used to select the
   * next queue to read based on priority.
   */
  private void initQueues(final SqsPriorityClientConfig config) {
    this.queueThresholds = new ArrayList<>(config.getWeightedQueues().size());
    this.queueUrls = new ArrayList<>(config.getWeightedQueues().size());

    config.getWeightedQueues().forEach((queueName, weight) -> {
      final GetQueueUrlResponse response = config.getSqsClient().getQueueUrl(GetQueueUrlRequest.builder()
              .queueName(queueName)
              .build());

      this.queueThresholds.add(1.0 - weight);
      this.queueUrls.add(response.queueUrl());
    });
  }
}
