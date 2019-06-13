package com.taranjot.Streaming.beans.kafkaMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

/**
 * handles json data for EnrichedAttributes in data.json file
 * 
 * @author sintara
 *
 */
@Data
@JsonIgnoreProperties(
  ignoreUnknown = true)
public class EnrichedAttributes {

  @JsonProperty("MATCH")
  private JsonNode match;
}
