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
package com.github.gregwhitaker.sqs.internal;

import java.time.Duration;
import java.util.concurrent.atomic.LongAdder;

/**
 * Stores information about queues managed by the client.
 */
public class PriorityQueueInfo {

  private final int index;
  private final String queueName;
  private final double weight;
  private final double threshold;
  private final String queueUrl;
  private final LongAdder emptyReceiveCnt = new LongAdder();
  private volatile Long timeoutExpiration;

  public PriorityQueueInfo(final int index,
                           final String queueName,
                           final String queueUrl,
                           final double weight,
                           final double threshold) {
    this.index = index;
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

  public int getIndex() {
    return index;
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
