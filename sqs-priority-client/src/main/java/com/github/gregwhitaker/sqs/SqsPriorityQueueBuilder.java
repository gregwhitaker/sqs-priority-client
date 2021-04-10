package com.github.gregwhitaker.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SqsPriorityQueueBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(SqsPriorityQueueBuilder.class);

  protected final SqsPriorityClientBuilder parentBuilder;

  SqsPriorityQueueBuilder(final SqsPriorityClientBuilder parentBuilder) {
    this.parentBuilder = parentBuilder;
  }

  public SqsPriorityQueueBuilder queue(final String queueName, final double weight) {
    final LinkedHashMap<String, Double> weightedQueues = parentBuilder.config.getWeightedQueues();
    weightedQueues.put(queueName, weight);

    // Sort the queues based on weight
    parentBuilder.config.setWeightedQueues(weightedQueues.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                    (e1, e2) -> e1, LinkedHashMap::new)));

    return this;
  }

  public SqsPriorityClientBuilder end() {
    validate();
    return parentBuilder;
  }

  private void validate() {

  }
}
