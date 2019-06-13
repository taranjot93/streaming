package com.taranjot.Streaming.beans.kafkaMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * handles json data for CustomAttributeField in data.json file
 * 
 * @author sintara
 *
 */
@Data
@JsonIgnoreProperties(
  ignoreUnknown = true)
public class CustomAttributesJsonMessage {

  private long roundsPlayed;
}
