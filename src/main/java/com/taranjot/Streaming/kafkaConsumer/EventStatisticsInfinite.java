package com.taranjot.Streaming.kafkaConsumer;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taranjot.Streaming.beans.Match;
import com.taranjot.Streaming.beans.Player;
import com.taranjot.Streaming.beans.kafkaMessage.CustomAttributesJsonMessage;
import com.taranjot.Streaming.beans.kafkaMessage.HeaderMessage;
import com.taranjot.Streaming.beans.kafkaMessage.MatchJsonMessage;
import com.taranjot.Streaming.beans.kafkaMessage.TypeAttributeMessageJson;
import com.taranjot.Streaming.config.AppConstants;

public class EventStatisticsInfinite implements EventConsumer {

  private HeaderMessage header;

  private CustomAttributesJsonMessage customAttributesJsonMessage;

  private MatchJsonMessage matchJsonMessage;

  private TypeAttributeMessageJson typeAttributeMessageJson;

  HashMap<String, Player> map = new HashMap<>();

  private String matchId;

  @Override
  public synchronized void accumulate(final Event event) {

    try {
      // map json data of header attribute in message received to pojo
      header = new ObjectMapper().treeToValue(event.getHeader(), HeaderMessage.class);

      // map json data of customAttributes attribute in message received to pojo
      customAttributesJsonMessage =
        new ObjectMapper().treeToValue(event.getCustomAttributes(), CustomAttributesJsonMessage.class);
      matchJsonMessage = new ObjectMapper().treeToValue(event.getMatch(), MatchJsonMessage.class);

      // map json data of typeAttributes attribute in message received to pojo
      typeAttributeMessageJson =
        new ObjectMapper().treeToValue(event.getTypeAttributes(), TypeAttributeMessageJson.class);

    }
    catch (final JsonProcessingException e) {
      e.printStackTrace();
    }

    final Player player;

    if (Objects.isNull(matchJsonMessage)) {
      matchId = typeAttributeMessageJson.getMatchId();
    }
    else {
      matchId = matchJsonMessage.getMatchId();
    }

    if (map.containsKey(header.getProfileId())) {
      player = map.get(header.getProfileId());
    }
    else {
      player = Player.builder()
        .profileId(header.getProfileId())
        .firstSeen(header.getServerDate())
        .lastSeen(header.getServerDate())
        .build();
      map.put(header.getProfileId(), player);
    }
    // every time when an event is recieved value of last seen is updated
    player.setLastSeen(header.getServerDate());

    // if match event increment players matches
    if (header.getEventType()
      .equals(AppConstants.EVENT_TYPE_NEW_MATCH)) {
      final HashMap<String, Match> playerMatches = Optional.ofNullable(player.getMatches())
        .orElse(new HashMap<String, Match>());
      playerMatches.put(matchId, Match.builder()
        .build());
      player.setMatches(playerMatches);
      player.incrementMatchesPlayed();
    }

    if (header.getEventType()
      .equals(AppConstants.EVENT_TYPE_ROUND_WIN)) {
      final Match match = getMatch(player);
      match.incrementRoundsWon();
    }

    // when recieve an event of match stop,get the value of total number of rounds
    // from the json message and calculate the ratio of win/loss for the match
    if (header.getEventType()
      .equals(AppConstants.EVENT_TYPE_MATCH_END)) {
      final Match match = getMatch(player);
      match.setTotalRounds(customAttributesJsonMessage.getRoundsPlayed());
      System.out.println(
        "Win Loss Ratio for player " + player.getProfileId() + "and match id " + matchId + "is" + match.winLossRatio());
    }

    map.forEach((key, value) -> System.out.println(value));
    System.out.println("---------------------------------------");
  }

  private Match getMatch(final Player player) {
    Match match;
    if (player.getMatches()
      .containsKey(matchId)) {
      // existing match
      match = player.getMatches()
        .get(matchId);

    }
    else {
      // new match
      match = Match.builder()
        .build();
      player.getMatches()
        .put(matchId, match);
    }
    return match;

  }

}
