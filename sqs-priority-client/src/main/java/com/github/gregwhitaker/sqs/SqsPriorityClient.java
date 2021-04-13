/**
 * Copyright 2021 Greg Whitaker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gregwhitaker.sqs;

import com.github.gregwhitaker.sqs.internal.PriorityQueueInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SQS client that receives messages from multiple queues based on weighted priority.
 */
public class SqsPriorityClient {
  private static final Logger LOG = LoggerFactory.getLogger(SqsPriorityClient.class);
  private static final Random RAND = new Random(System.currentTimeMillis());

  private final SqsPriorityClientConfig config;
  private final List<PriorityQueueInfo> queues = new ArrayList<>();
  private final Cache<String, Integer> receiptHandleCache;

  SqsPriorityClient(SqsPriorityClientConfig config) {
    this.config = config;
    this.receiptHandleCache = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    initialize();
  }

  /**
   * Gets the builder for constructing an instance of {@link SqsPriorityClient}.
   *
   * @param sqs sqs client
   * @return builder for {@link SqsPriorityClient}
   */
  public static SqsPriorityClientBuilder builder(final SqsClient sqs) {
    return new SqsPriorityClientBuilder(sqs);
  }

  /**
   * Receives a stream of messages that never completes.
   *
   * @return a {@link Flux} of {@link Message}
   */
  public Flux<Message> receiveMessages() {
    return receiveMessages(Long.MAX_VALUE);
  }

  /**
   * Receives a finite stream of the specified number of messages and then completes.
   *
   * @param count number of messages to receive
   * @return a {@link Flux} of {@link Message}
   */
  public Flux<Message> receiveMessages(final long count) {
    return Flux.create(fluxSink -> {
      if (count < config.getMaxNumberOfMessages()) {
        Flux.error(new IllegalArgumentException("Requested message count must be greater than configured maxNumberOfMessages"));
      }

      final AtomicLong rxCnt = new AtomicLong();

      while (rxCnt.get() + config.getMaxNumberOfMessages() <= count) {
        final PriorityQueueInfo queue = nextQueue();

        final ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queue.getQueueUrl())
                .maxNumberOfMessages(config.getMaxNumberOfMessages())
                .build();

        final ReceiveMessageResponse receiveMessageResponse = config.getSqsClient().receiveMessage(request);

        if (receiveMessageResponse.hasMessages()) {
          receiveMessageResponse.messages().forEach(message -> {
            receiptHandleCache.put(message.receiptHandle(), queue.getIndex());
            rxCnt.incrementAndGet();
            fluxSink.next(message);
          });
        } else {
          // No messages received
          if (queue.getEmptyReceiveCnt().sum() == config.getMaxEmptyReceiveCount()) {
            queue.timeout(config.getEmptyReceiveTimeout());
          } else {
            queue.incrementEmptyReceive();
          }
        }
      }

      // Complete the stream after the specified number of messages have been emitted
      fluxSink.complete();
    });
  }

  /**
   * Deletes a message from the queue.
   *
   * @param receiptHandle message receipt handle
   * @return a {@link Mono} of {@link Void}
   */
  public Mono<Void> deleteMessage(final String receiptHandle) {
    return Mono.fromSupplier(() -> {
      final Integer idx = receiptHandleCache.getIfPresent(receiptHandle);
      if (idx != null) {
        try {
          config.getSqsClient().deleteMessage(DeleteMessageRequest.builder()
                  .receiptHandle(receiptHandle)
                  .queueUrl(queues.get(idx).getQueueUrl())
                  .build());

          receiptHandleCache.invalidate(receiptHandle);
        } catch (Exception e) {
          LOG.error("Unable to delete message. [receiptHandle: '{}', queueUrl: '{}']", receiptHandle, queues.get(idx).getQueueUrl());
          throw new RuntimeException(String.format("Unable to delete message. [receiptHandle: '%s', queueUrl: '%s']", receiptHandle, queues.get(idx).getQueueUrl()), e);
        }
      }

      return null;
    });
  }

  /**
   * Selects the next queue from which to poll for messages.
   *
   * @return the {@link PriorityQueueInfo} for the next queue
   */
  private PriorityQueueInfo nextQueue() {
    final double nextRand = RAND.nextDouble();

    while (true) {
      // Select queue based on random threshold value
      PriorityQueueInfo nextQueue = null;
      for (PriorityQueueInfo queueInfo : queues) {
        if (nextRand >= queueInfo.getThreshold()) {
          nextQueue = queueInfo;
          break;
        }
      }

      // In the event no queue was selected above then select the highest priority queue
      if (nextQueue == null) {
        nextQueue = queues.get(queues.size() - 1);
      }

      if (nextQueue.isAvailable()) {
        return nextQueue;
      } else {
        // If the selected queue is not available then backoff to the next highest priority queue
        PriorityQueueInfo tmpNextQueue = nextQueue;
        while (true) {
          if (tmpNextQueue.getIndex() == 0) {
            tmpNextQueue = queues.get(queues.size() - 1);
          } else {
            tmpNextQueue = queues.get(tmpNextQueue.getIndex() - 1);
          }

          if (tmpNextQueue.getIndex() == nextQueue.getIndex()) {
            try {
              // In the event no queues are available sleep for a short period to prevent AWS rate-limiting
              Thread.sleep(1_000);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }

            // Return to the main queue selection loop and attempt to get a new queue
            break;
          } else {
            if (tmpNextQueue.isAvailable()) {
              return tmpNextQueue;
            }
          }
        }
      }
    }
  }

  /**
   * Initializes the client.
   */
  private void initialize() {
    // Initialize the priority queues managed by the client
    int curIdx = 0;
    for (Map.Entry<String, Double> entry : config.getWeightedQueues().entrySet()) {
      try {
        final String queueName = entry.getKey();
        final Double weight = entry.getValue();

        final GetQueueUrlResponse response = config.getSqsClient().getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build());

        this.queues.add(new PriorityQueueInfo(curIdx, queueName, response.queueUrl(), weight, 1.0 - weight));
        curIdx++;
      } catch (SqsException e) {
        LOG.error("Unable to initialize queue [queueName: '{}']", entry.getKey());
        throw new RuntimeException(String.format("Unable to initialize queue [queueName: '%s']", entry.getKey()), e);
      }
    }
  }
}
