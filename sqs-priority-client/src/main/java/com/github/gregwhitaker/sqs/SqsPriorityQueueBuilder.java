package com.github.gregwhitaker.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builder that creates the configuration for the priority queues.
 */
public class SqsPriorityQueueBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(SqsPriorityQueueBuilder.class);

  protected final SqsPriorityClientBuilder parentBuilder;

  SqsPriorityQueueBuilder(final SqsPriorityClientBuilder parentBuilder) {
    this.parentBuilder = parentBuilder;
  }

  /**
   * Adds a weighted queue from which to send and receive messages.
   *
   * @param queueName name of the queue
   * @param weight queue weight (must be a value between 0.0 and 1.0)
   * @return this {@link SqsPriorityQueueBuilder}
   */
  public SqsPriorityQueueBuilder queue(final String queueName, final double weight) {
    final LinkedHashMap<String, Double> weightedQueues = parentBuilder.config.getWeightedQueues();
    weightedQueues.put(queueName, weight);

    // Sort the queues based on weight (highest weight first)
    parentBuilder.config.setWeightedQueues(weightedQueues.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                    (e1, e2) -> e1, LinkedHashMap::new)));

    return this;
  }

  /**
   * Ends configuration of the builder and returns to the parent builder.
   *
   * @return the {@link SqsPriorityClientBuilder}
   */
  public SqsPriorityClientBuilder end() {
    validate();
    return parentBuilder;
  }

  /**
   * Validates the builder configuration.
   */
  private void validate() {
    LOG.debug("Validating priority queue configuration");

    // Validate that queue weights equal 1
    final double summedWeights = parentBuilder.config.getWeightedQueues().values()
            .stream()
            .mapToDouble(v -> v)
            .sum();

    if (summedWeights != 1.0) {
      throw new IllegalArgumentException("Queue weights must total 1.0");
    }

    // Validate that there are no duplicate queue weights
    final Set<Double> dupeWeights = parentBuilder.config.getWeightedQueues().values()
            .stream()
            .filter(i -> Collections.frequency(parentBuilder.config.getWeightedQueues().values(), i) > 1)
            .collect(Collectors.toSet());

    if (!dupeWeights.isEmpty()) {
      throw new IllegalArgumentException("Queue weights must be unique");
    }
  }
}
