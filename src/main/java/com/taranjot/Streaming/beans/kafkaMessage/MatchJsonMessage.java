package com.taranjot.Streaming.beans.kafkaMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * handles json data for Match in data.json file
 * 
 * @author sintara
 *
 */
@Data
@JsonIgnoreProperties(
  ignoreUnknown = true)
public class MatchJsonMessage {

  @JsonProperty("contextName")
  private String matchId;
}
