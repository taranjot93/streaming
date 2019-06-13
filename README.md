# streaming

Handle kafka streams

First time seen = service identify the first time seen of a player by checking if this is the first event 
application is receving for particuler profile ID.Service saves server date from that kafka message as first time seen of the player.

Last Time seen = every time an event is recevied from kafka queue, service updates last seen of the player as serverDate from
kafka message recevied.

Number of Matches = Assumption: I Assumed that every time player starts a match, an event with eventType context.start.Match is received.
every time an context.start.Match event is recevied as kafka message, service increments number of matches played by 1.

win/loss ratio = Assumption: every time players wins a round in a match , an event with eventType custom.RoundWin is received.
when match finished, total number of rounds are received as part of context.stop.Match event.
service stores round win for a match for each player in hashmap and calculates ration of win/loss every time a match-end event is received.
