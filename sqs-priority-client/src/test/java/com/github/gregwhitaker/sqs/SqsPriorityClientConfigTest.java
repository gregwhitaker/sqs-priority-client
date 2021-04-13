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

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqsPriorityClientConfigTest {

  @Test
  public void defaultMaxNumberOfMessagesShouldBe10() {
    // Given
    final SqsPriorityClientConfig config = new SqsPriorityClientConfig();

    // Then
    assertEquals(10, config.getMaxNumberOfMessages());
  }

  @Test
  public void defaultMaxEmptyReceiveCountShouldBe3() {
    // Given
    final SqsPriorityClientConfig config = new SqsPriorityClientConfig();

    // Then
    assertEquals(3, config.getMaxEmptyReceiveCount());
  }

  @Test
  public void defaultEmptyReceiveTimeoutShouldBe10Seconds() {
    // Given
    final SqsPriorityClientConfig config = new SqsPriorityClientConfig();

    // Then
    assertEquals(Duration.ofSeconds(10), config.getEmptyReceiveTimeout());
  }

  @Test
  public void shouldDefaultWeightedQueuesToEmptyIfNoneSpecified() {
    // Given
    final SqsPriorityClientConfig config = new SqsPriorityClientConfig();

    // Then
    assertTrue(config.getWeightedQueues().isEmpty());
  }
}
