package com.github.gregwhitaker.sqs.internal;

import java.time.Duration;
import java.util.concurrent.atomic.LongAdder;

public class PriorityQueueInfo {

  private final String queueName;
  private final double weight;
  private final double threshold;
  private final String queueUrl;
  private final LongAdder emptyReceiveCnt = new LongAdder();
  private volatile Long timeoutExpiration;

  public PriorityQueueInfo(final String queueName,
                           final String queueUrl,
                           final double weight,
                           final double threshold) {
    this.queueName = queueName;
    this.queueUrl = queueUrl;
    this.weight = weight;
    this.threshold = threshold;
  }

  public void incrementEmptyReceive() {
    emptyReceiveCnt.add(1);
  }

  public void timeout(final Duration duration) {
    this.timeoutExpiration = System.currentTimeMillis() + Duration.ZERO.toMillis();
  }

  public boolean isAvailable() {
    if (timeoutExpiration == null) {
      return true;
    }

    if (timeoutExpiration > System.currentTimeMillis()) {
      return true;
    } else {
      emptyReceiveCnt.reset();
      timeoutExpiration = null;
      return false;
    }
  }

  public String getQueueName() {
    return queueName;
  }

  public double getWeight() {
    return weight;
  }

  public double getThreshold() {
    return threshold;
  }

  public String getQueueUrl() {
    return queueUrl;
  }

  public LongAdder getEmptyReceiveCnt() {
    return emptyReceiveCnt;
  }
}
