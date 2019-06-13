package com.taranjot.Streaming.beans;

import java.util.HashMap;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Player {

  private String lastSeen;

  private String profileId;

  private String firstSeen;

  private long matchesPlayed;

  // holds the matches played by player
  @Builder.Default
  private HashMap<String, Match> matches = new HashMap<String, Match>();

  public void incrementMatchesPlayed() {
    matchesPlayed++;
  }

}
