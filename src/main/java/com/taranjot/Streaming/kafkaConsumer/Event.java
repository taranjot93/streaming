package com.taranjot.Streaming.kafkaConsumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@JsonIgnoreProperties(
  ignoreUnknown = true)
@Data
public class Event {

  private JsonNode header;

  @JsonProperty("MATCH")
  private JsonNode match;

  private JsonNode customAttributes;

  private JsonNode typeAttributes;

  private JsonNode enrichedAttributes;

}
