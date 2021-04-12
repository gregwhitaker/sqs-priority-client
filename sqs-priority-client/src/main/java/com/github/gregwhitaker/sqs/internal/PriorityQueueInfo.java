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

  /**
   * Creates a new instance of {@link PriorityQueueInfo}.
   *
   * @param index queue index ranking
   * @param queueName queue name
   * @param queueUrl queue url
   * @param weight queue weight
   * @param threshold queue priority threshold
   */
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

  /**
   * Increments the empty receive count of this queue by 1.
   */
  public void incrementEmptyReceive() {
    emptyReceiveCnt.add(1);
  }

  /**
   * Marks the queue as timed out for the specified duration. When the queue is in timeout it will
   * not be checked for messages.
   *
   * @param duration length of timeout
   */
  public void timeout(final Duration duration) {
    this.timeoutExpiration = System.currentTimeMillis() + Duration.ZERO.toMillis();
  }

  /**
   * Checks to see if the queue is available for reads.
   *
   * @return <code>true</code> if the queue is available; otherwise <code>false</code>
   */
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

  /**
   * Gets the index ranking of the queue. Higher number signifies higher priority.
   *
   * @return index ranking of queue
   */
  public int getIndex() {
    return index;
  }

  /**
   * Gets the name of the queue.
   *
   * @return queue name
   */
  public String getQueueName() {
    return queueName;
  }

  /**
   * Gets the configured weight of the queue.
   *
   * @return queue priority weight
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Gets the calculated threshold for the queue. The threshold is 1.0 - weight.
   *
   * @return queue priority threshold
   */
  public double getThreshold() {
    return threshold;
  }

  /**
   * Gets the queue url.
   *
   * @return queue url
   */
  public String getQueueUrl() {
    return queueUrl;
  }

  /**
   * Gets the empty receive count of the queue.
   *
   * @return number of empty receives
   */
  public LongAdder getEmptyReceiveCnt() {
    return emptyReceiveCnt;
  }
}
