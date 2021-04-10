package com.github.gregwhitaker.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 *
 */
class SqsPriorityClientBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(SqsPriorityClientBuilder.class);

  protected SqsPriorityClientConfig config = new SqsPriorityClientConfig();

  public SqsPriorityClientBuilder(final SqsClient sqsClient) {
    this.config.setSqsClient(sqsClient);
  }

  public SqsPriorityClientBuilder withMaxNumberOfMessages(int maxMessages) {
    config.setMaxNumberOfMessages(maxMessages);
    return this;
  }

  public SqsPriorityQueueBuilder withQueues() {
    return new SqsPriorityQueueBuilder(this);
  }

  public SqsPriorityClient build() {
    return new SqsPriorityClient(config);
  }
}
