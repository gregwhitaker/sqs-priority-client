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

import java.time.Duration;

/**
 * Configures and builds an instance of {@link SqsPriorityClient}.
 */
public class SqsPriorityClientBuilder {
  protected SqsPriorityClientConfig config = new SqsPriorityClientConfig();

  public SqsPriorityClientBuilder(final SqsClient sqsClient) {
    this.config.setSqsClient(sqsClient);
  }

  /**
   * Maximum number of messages to retrieve from a queue in a single read. The maximum allowed by SQS is 10.
   *
   * @param maxMessages number of messages to retrieve from a queue in a single read
   * @return this {@link SqsPriorityClientBuilder}
   */
  public SqsPriorityClientBuilder withMaxNumberOfMessages(int maxMessages) {
    config.setMaxNumberOfMessages(maxMessages);
    return this;
  }

  /**
   * Maximum number of empty reads from a queue before reading is temporarily paused to prevent
   * rate-limiting by the AWS API. Default is 10.
   *
   * @param maxEmptyReceiveCount number of empty reads permitted before temporarily pausing queue read
   * @return this {@link SqsPriorityClientBuilder}
   */
  public SqsPriorityClientBuilder withMaxEmptyReceiveCount(int maxEmptyReceiveCount) {
    config.setMaxEmptyReceiveCount(maxEmptyReceiveCount);
    return this;
  }

  /**
   * Sets the duration of the temporary read pause triggered when the maximum number of empty receives is
   * encountered for a queue. Default is 1 second.
   *
   * @param emptyReceiveTimeout timeout duration
   * @return this {@link SqsPriorityClientBuilder}
   */
  public SqsPriorityClientBuilder withEmptyReceiveTimeout(Duration emptyReceiveTimeout) {
    config.setEmptyReceiveTimeout(emptyReceiveTimeout);
    return this;
  }

  /**
   * Adds priority queues from which to read messages.
   *
   * @return a {@link SqsPriorityQueueBuilder}
   */
  public SqsPriorityQueueBuilder withQueues() {
    return new SqsPriorityQueueBuilder(this);
  }

  /**
   * Creates an instance of {@link SqsPriorityClient}.
   *
   * @return an {@link SqsPriorityClient}
   */
  public SqsPriorityClient build() {
    validate();
    return new SqsPriorityClient(config);
  }

  /**
   * Validates the builder configuration.
   */
  private void validate() {
    if (config.getMaxNumberOfMessages() <= 0 || config.getMaxNumberOfMessages() > 10) {
      throw new IllegalArgumentException("Max number of messages must be an integer from 1 to 10");
    }

    if (config.getMaxEmptyReceiveCount() <= 0) {
      throw new IllegalArgumentException("Max receive count must be a positive integer");
    }

    if (config.getEmptyReceiveTimeout() == null) {
      throw new IllegalArgumentException("Empty receive count cannot be null");
    }
  }
}
