package com.taranjot.Streaming.beans;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Match class to hadle mathes assosiated with player
 *
 * @author sintara
 *
 */
@Data
@ToString
@Builder
public class Match {

  private long roundsWon;

  private long totalRounds;

  public void incrementRoundsWon() {
    roundsWon++;
  }

  /**
   * calculates win/loss ration of this match
   * 
   * @return win/loss ratio
   */
  public float winLossRatio() {

    if (totalRounds <= 0) {
      return 0F;
    }

    return roundsWon / Float.valueOf(totalRounds);

  }
}
