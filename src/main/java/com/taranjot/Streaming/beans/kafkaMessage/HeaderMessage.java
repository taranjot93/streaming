package com.taranjot.Streaming.beans.kafkaMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * handles json data for header in data.json file
 * 
 * @author sintara
 *
 */
@Data
@JsonIgnoreProperties(
  ignoreUnknown = true)
public class HeaderMessage {

  private String eventType;

  private String profileId;

  private String serverDate;

}
