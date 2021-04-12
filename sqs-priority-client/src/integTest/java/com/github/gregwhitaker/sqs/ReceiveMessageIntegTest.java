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
