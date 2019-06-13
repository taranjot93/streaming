package com.taranjot.Streaming.kafkaConsumer;

import java.util.Objects;

@FunctionalInterface
public interface EventConsumer {

  void accumulate(Event event);

  default EventConsumer andThen(final EventConsumer after) {
    Objects.requireNonNull(after);
    return (final Event t) -> {
      accumulate(t);
      after.accumulate(t);
    };
  }
}
