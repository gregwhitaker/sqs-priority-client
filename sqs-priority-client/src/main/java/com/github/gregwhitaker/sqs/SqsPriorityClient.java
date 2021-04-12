package com.github.gregwhitaker.sqs;

import com.github.gregwhitaker.sqs.internal.PriorityQueueInfo;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

/**
 * SQS client that receives messages from multiple queues based on weighted priority.
 */
public class SqsPriorityClient {
  private static final Logger LOG = LoggerFactory.getLogger(SqsPriorityClient.class);
  private static final Random RAND = new Random(System.currentTimeMillis());

  private final SqsPriorityClientConfig config;
  private final List<PriorityQueueInfo> queues = new ArrayList<>();

  SqsPriorityClient(SqsPriorityClientConfig config) {
    this.config = config;
    initialize();
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
    return Flux.create(fluxSink -> {
      final LongAdder rxCnt = new LongAdder();
      while (rxCnt.sum() <= count) {
        final PriorityQueueInfo queue = nextQueue();

        final ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queue.getQueueUrl())
                .maxNumberOfMessages(config.getMaxNumberOfMessages())
                .build();

        final ReceiveMessageResponse receiveMessageResponse = config.getSqsClient().receiveMessage(request);
        if (receiveMessageResponse.hasMessages()) {
          receiveMessageResponse.messages().forEach(message -> {
            fluxSink.next(message);
            rxCnt.add(1);
          });
        } else {
          if (queue.getEmptyReceiveCnt().sum() == 10) {
            queue.timeout(Duration.ofSeconds(1));
          } else {
            queue.incrementEmptyReceive();
          }
        }
      }

      fluxSink.complete();
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

  private PriorityQueueInfo nextQueue() {
    final double nextRand = RAND.nextDouble();

    while (true) {
      for (int cnt = 0; cnt < queues.size(); cnt++) {
        if (nextRand >= queues.get(cnt).getThreshold() && queues.get(cnt).isAvailable()) {
          return queues.get(cnt);
        }
      }

      if (queues.get(queues.size() - 1).isAvailable()) {
        return queues.get(queues.size() - 1);
      } else {
        try {
          Thread.sleep(1_000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Initializes the client.
   */
  private void initialize() {
    // Initialize the priority queues managed by the client
    config.getWeightedQueues().forEach((queueName, weight) -> {
      final GetQueueUrlResponse response = config.getSqsClient().getQueueUrl(GetQueueUrlRequest.builder()
              .queueName(queueName)
              .build());

      this.queues.add(new PriorityQueueInfo(queueName, response.queueUrl(), weight, 1.0 - weight));
    });
  }
}
