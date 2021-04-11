package com.github.gregwhitaker.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

public class SqsPriorityClient {
  private static final Logger LOG = LoggerFactory.getLogger(SqsPriorityClient.class);

  private final SqsPriorityClientConfig config;

  SqsPriorityClient(SqsPriorityClientConfig config) {
    this.config = config;
  }

  /**
   * Gets the builder for constructing an instance of {@link SqsPriorityClient}.
   *
   * @param sqs
   * @return
   */
  public static SqsPriorityClientBuilder builder(final SqsClient sqs) {
    return new SqsPriorityClientBuilder(sqs);
  }

  public Flux<Message> receiveMessages() {
    return receiveMessages(Long.MAX_VALUE);
  }

  public Flux<Message> receiveMessages(final long count) {
    return null;
  }

  public Mono<Void> deleteMessage(final String receiptHandle) {
    return null;
  }
}
