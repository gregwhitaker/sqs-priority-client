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
import java.util.LinkedHashMap;

/**
 * SqsPriorityClient configuration
 */
public class SqsPriorityClientConfig {

  /**
   * Default maximum number of SQS messages to retrieve at a time.
   */
  public int DEFAULT_MAX_NUMBER_OF_MESSAGES = 10;

  /**
   * Default maximum number of empty receives encountered on a queue before reading is temporarily paused.
   */
  public int DEFAULT_MAX_EMPTY_RECEIVE_COUNT = 3;

  /**
   * Default duration queue reading is paused for a queue when the max empty receives are encountered.
   */
  public Duration DEFAULT_EMPTY_RECEIVE_TIMEOUT = Duration.ofSeconds(10);

  private SqsClient sqsClient;
  private int maxNumberOfMessages = DEFAULT_MAX_NUMBER_OF_MESSAGES;
  private int maxEmptyReceiveCount = DEFAULT_MAX_EMPTY_RECEIVE_COUNT;
  private Duration emptyReceiveTimeout = DEFAULT_EMPTY_RECEIVE_TIMEOUT;
  private LinkedHashMap<String, Double> weightedQueues;

  /**
   * Gets the AWS sqs client.
   *
   * @return sqs client
   */
  public SqsClient getSqsClient() {
    return sqsClient;
  }

  /**
   * Sets the AWS sqs client.
   *
   * @param sqsClient sqs client
   */
  public void setSqsClient(SqsClient sqsClient) {
    this.sqsClient = sqsClient;
  }

  /**
   * Gets the maximum number of messages to retrieve from a queue in a single read.
   *
   * @return max number of messages
   */
  public int getMaxNumberOfMessages() {
    return maxNumberOfMessages;
  }

  /**
   * Sets the maximum number of messages to retrieve from a queue in a single read.
   *
   * @param maxNumberOfMessages max number of messages
   */
  public void setMaxNumberOfMessages(int maxNumberOfMessages) {
    this.maxNumberOfMessages = maxNumberOfMessages;
  }

  /**
   * Gets the maximum number of empty receives on a queue before reading is paused.
   *
   * @return max number of empty receives
   */
  public int getMaxEmptyReceiveCount() {
    return maxEmptyReceiveCount;
  }

  /**
   * Sets the maximum number of empty receives on a queue before reading is paused.
   *
   * @param maxEmptyReceiveCount max number of empty receives
   */
  public void setMaxEmptyReceiveCount(int maxEmptyReceiveCount) {
    this.maxEmptyReceiveCount = maxEmptyReceiveCount;
  }

  /**
   * Gets the timeout for empty receives.
   *
   * @return timeout
   */
  public Duration getEmptyReceiveTimeout() {
    return emptyReceiveTimeout;
  }

  /**
   * Sets the timeout for empty receives.
   *
   * @param emptyReceiveTimeout timeout
   */
  public void setEmptyReceiveTimeout(Duration emptyReceiveTimeout) {
    this.emptyReceiveTimeout = emptyReceiveTimeout;
  }

  /**
   * Gets the priority weighted queues.
   *
   * @return a {@link LinkedHashMap} of queue names to weights
   */
  public LinkedHashMap<String, Double> getWeightedQueues() {
    if (weightedQueues == null) {
      this.weightedQueues = new LinkedHashMap<>();
    }

    return weightedQueues;
  }

  /**
   * Sets the priority weighted queues.
   *
   * @param weightedQueues a {@link LinkedHashMap} of queue names to weights
   */
  public void setWeightedQueues(LinkedHashMap<String, Double> weightedQueues) {
    this.weightedQueues = weightedQueues;
  }
}
