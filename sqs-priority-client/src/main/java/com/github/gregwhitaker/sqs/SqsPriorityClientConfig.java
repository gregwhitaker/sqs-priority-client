package com.github.gregwhitaker.sqs;

import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.LinkedHashMap;

public class SqsPriorityClientConfig {
  public int DEFAULT_MAX_NUMBER_OF_MESSAGES = 10;

  private SqsClient sqsClient;
  private int maxNumberOfMessages = DEFAULT_MAX_NUMBER_OF_MESSAGES;
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
