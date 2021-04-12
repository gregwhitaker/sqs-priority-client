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

  public int DEFAULT_MAX_EMPTY_RECEIVE_COUNT = 10;

  public Duration DEFAULT_EMPTY_RECEIVE_TIMEOUT = Duration.ofSeconds(1);

  private SqsClient sqsClient;
  private int maxNumberOfMessages = DEFAULT_MAX_NUMBER_OF_MESSAGES;
  private int maxEmptyReceiveCount = DEFAULT_MAX_EMPTY_RECEIVE_COUNT;
  private Duration emptyReceiveTimeout = DEFAULT_EMPTY_RECEIVE_TIMEOUT;
  private LinkedHashMap<String, Double> weightedQueues;

  public SqsClient getSqsClient() {
    return sqsClient;
  }

  public void setSqsClient(SqsClient sqsClient) {
    this.sqsClient = sqsClient;
  }

  public int getMaxNumberOfMessages() {
    return maxNumberOfMessages;
  }

  public void setMaxNumberOfMessages(int maxNumberOfMessages) {
    this.maxNumberOfMessages = maxNumberOfMessages;
  }

  public int getMaxEmptyReceiveCount() {
    return maxEmptyReceiveCount;
  }

  public void setMaxEmptyReceiveCount(int maxEmptyReceiveCount) {
    this.maxEmptyReceiveCount = maxEmptyReceiveCount;
  }

  public Duration getEmptyReceiveTimeout() {
    return emptyReceiveTimeout;
  }

  public void setEmptyReceiveTimeout(Duration emptyReceiveTimeout) {
    this.emptyReceiveTimeout = emptyReceiveTimeout;
  }

  public LinkedHashMap<String, Double> getWeightedQueues() {
    if (weightedQueues == null) {
      this.weightedQueues = new LinkedHashMap<>();
    }

    return weightedQueues;
  }

  public void setWeightedQueues(LinkedHashMap<String, Double> weightedQueues) {
    this.weightedQueues = weightedQueues;
  }
}
