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

    // parse data as java objects
    parseJSON(event);

    final Player player;

    // finds matchId from the kafka message, assumed that context_name is match_id
    // in kafka message, which is there in Match JsonNode for some events and in
    // typeAttribute JsonNode for some other events. Match id if needed to keep
    // track of rounds win in order to finds win/loss ratio.

    if (Objects.isNull(matchJsonMessage)) {
      matchId = typeAttributeMessageJson.getMatchId();
    }
    else {
      matchId = matchJsonMessage.getMatchId();
    }

    // find player object in the map using profile id received from kafka message.
    // create and add a new player if it doesnt exist in map.
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

    // update value of last seen time of player with server date received from kafka
    // message, so that last event received would be the last seen of the player.
    player.setLastSeen(header.getServerDate());

    // add a new entry of match for player using match id as key in the map and
    // updates total number of matches played by player as well. It is assumed that
    // everytime player starts a match ,an event with event type context.start.Match
    // is received.
    if (header.getEventType()
      .equals(AppConstants.EVENT_TYPE_NEW_MATCH)) {
      final HashMap<String, Match> playerMatches = Optional.ofNullable(player.getMatches())
        .orElse(new HashMap<String, Match>());
      playerMatches.put(matchId, Match.builder()
        .build());
      player.setMatches(playerMatches);
      player.incrementMatchesPlayed();
    }

    // get current match for player using match_id and increments number of rounds
    // win for that match. It is assumed that an event of eventType custom.roundWin
    // is received when player wins a round in a match.
    if (header.getEventType()
      .equals(AppConstants.EVENT_TYPE_ROUND_WIN)) {
      final Match match = getMatch(player);
      match.incrementRoundsWon();
    }

    // when receive an event indicating finishing of match, get the value of total
    // number of rounds
    // from the json message and calculate the ratio of win/loss for the match.
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

  /**
   * finds match related to player using Match ID
   *
   * @param player
   *          player identified by profile Id
   * @return Match match related to current event's Match Id
   */
  private Match getMatch(final Player player) {
    Match match;
    if (player.getMatches()
      .containsKey(matchId)) {
      // existing match
      match = player.getMatches()
        .get(matchId);
    }

    else {
      match = Match.builder()
        .build();
      player.getMatches()
        .put(matchId, match);
    }
    return match;
  }

  /**
   * parse json messages to pojo
   *
   * @param event
   *          kafka message received
   */
  private void parseJSON(final Event event) {
    try {
      header = new ObjectMapper().treeToValue(event.getHeader(), HeaderMessage.class);

      customAttributesJsonMessage =
        new ObjectMapper().treeToValue(event.getCustomAttributes(), CustomAttributesJsonMessage.class);
      matchJsonMessage = new ObjectMapper().treeToValue(event.getMatch(), MatchJsonMessage.class);

      typeAttributeMessageJson =
        new ObjectMapper().treeToValue(event.getTypeAttributes(), TypeAttributeMessageJson.class);

    }
    catch (final JsonProcessingException e) {
      System.out.println(e);
    }
  }

}
