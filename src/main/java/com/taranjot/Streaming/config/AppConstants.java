package com.taranjot.Streaming.config;

/**
 * Class to handle all the constants values
 *
 * @author sintara
 *
 */
public class AppConstants {

  public static final String APP_ID = "player-data-streaming-app";

  public static final String EVENT_TYPE_NEW_MATCH = "context.start.MATCH";

  public static final String EVENT_TYPE_ROUND_WIN = "custom.RoundWin";

  public static final String EVENT_TYPE_MATCH_END = "context.stop.MATCH";

  public static final String KAFKA_SERVER_ADDRESS = "192.168.99.100:9092";

  public static final String ZOOKEEPER_SERVER_ADDRESS = "192.168.99.100:2181";

  public static final String KAFKA_TOPIC_NAME = "sampleData";

}
