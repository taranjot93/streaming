package com.taranjot.Streaming;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import com.taranjot.Streaming.config.AppConstants;
import com.taranjot.Streaming.config.JsonPOJODeserializer;
import com.taranjot.Streaming.config.JsonPOJOSerializer;
import com.taranjot.Streaming.kafkaConsumer.Event;
import com.taranjot.Streaming.kafkaConsumer.EventStatisticsInfinite;

public class App {

  public static void main(final String[] args) {
    System.out.println("Kafka Streams For Game Players");

    // Create an instance of StreamsConfig from the Properties instance
    final StreamsConfig config = new StreamsConfig(getProperties());
    final Serde<String> stringSerde = Serdes.String();

    // define EventMessageSerde
    final Map<String, Object> serdeProps = new HashMap<>();

    final Serializer<Event> playerMessageSerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", Event.class);
    playerMessageSerializer.configure(serdeProps, false);

    final Deserializer<Event> playerMessageDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", Event.class);
    playerMessageDeserializer.configure(serdeProps, false);

    final Serde<Event> playerMessageSerde = Serdes.serdeFrom(playerMessageSerializer, playerMessageDeserializer);

    // building Kafka Streams Model
    final KStreamBuilder kStreamBuilder = new KStreamBuilder();

    // the source of the streaming analysis is the topic with event messages
    final KStream<String, Event> playersStream =
      kStreamBuilder.stream(stringSerde, playerMessageSerde, AppConstants.KAFKA_TOPIC_NAME);

    // THIS IS THE CORE OF THE STREAMING ANALYTICS
    // number of matcher per player

    // Accumulator
    final EventStatisticsInfinite esi = new EventStatisticsInfinite();

    playersStream.foreach((key, event) -> {
      esi.accumulate(event);
    });

    System.out.println("Starting Kafka Streams Game Players");
    final KafkaStreams kafkaStreams = new KafkaStreams(kStreamBuilder, config);
    kafkaStreams.start();
    System.out.println("Now started players stream");

  }

  private static Properties getProperties() {

    final Properties settings = new Properties();

    // Set a few key parameters
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConstants.APP_ID);

    // Kafka bootstrap server (broker to talk to)
    // running Kafka, port 9092 is where the (single) broker listens
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConstants.KAFKA_SERVER_ADDRESS);

    // Apache ZooKeeper instance keeping watch over the Kafka cluster
    // port 2181 is where the ZooKeeper listens
    settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, AppConstants.ZOOKEEPER_SERVER_ADDRESS);

    // default serdes for serialzing and deserializing key and value from and to
    // streams in case no specific Serde is specified
    settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String()
      .getClass()
      .getName());
    settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String()
      .getClass()
      .getName());
    settings.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\temp");
    // to work around exception Exception in thread "StreamThread-1"
    // java.lang.IllegalArgumentException: Invalid timestamp -1
    // at
    // org.apache.kafka.clients.producer.ProducerRecord.<init>(ProducerRecord.java:60)
    // see: https://groups.google.com/forum/#!topic/confluent-platform/5oT0GRztPBo
    settings.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
    return settings;
  }

}
