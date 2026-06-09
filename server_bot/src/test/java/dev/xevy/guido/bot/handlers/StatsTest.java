package dev.xevy.guido.bot.handlers;

import dev.xevy.guido.bot.GuidoTestRuntime;
import java.io.IOException;
import java.util.Map;
import me.googas.api.Requests;
import me.googas.api.immutable.ImmutableLadder;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.stats.LeaderboardEntry;
import me.googas.api.stats.Stats;
import me.googas.net.api.exception.MessengerListenFailException;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.builders.MapBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatsTest {

  @Test
  public void testSaveStats() throws IOException, MessengerListenFailException {
    GuidoTestRuntime runtime = GuidoTestRuntime.createRuntime();
    JsonClient jsonClient = runtime.joinWithClient();
    MinecraftLinkable linkable = runtime.createMinecraftLinkable("Foo", false);
    String context = "junit";
    Stats stats = runtime.getLoader().getStats().getForMinecraftLink(linkable, context);
    Map<String, Double> update =
        MapBuilder.of("kills", 3d)
            .put("assists", 1d)
            .put("deaths", 6d)
            .put("defense", -10d)
            .build();
    boolean empty = stats.getMap().isEmpty();
    Assertions.assertTrue(empty, "Stats should be empty before update");
    Requests.MinecraftLinks.saveStats(linkable.getId(), "junit", update).send(jsonClient);
    Stats statsAfter = runtime.getLoader().getStats().getForMinecraftLink(linkable, context);
    boolean emptyAfterUpdate = statsAfter.getMap().isEmpty();
    Assertions.assertFalse(emptyAfterUpdate, "Stats should not be empty after update");
  }

  @Test
  public void testStaleObjectRecovers() {
    GuidoTestRuntime runtime = GuidoTestRuntime.createRuntime();
    MinecraftLinkable linkable = runtime.createMinecraftLinkable("Foo", false);
    String context = "junit";
    Stats staleStats = runtime.getLoader().getStats().getForMinecraftLink(linkable, context);
    Stats otherStats = runtime.getLoader().getStats().getForMinecraftLink(linkable, context);
    int base = 500;
    int winnersDifference = 16;
    Ladder ladder =
        new ImmutableLadder(2, base, 2, 1, 1, "junit", MinecraftTeamSelectionType.RANDOM);
    otherStats.increaseElo(ladder, winnersDifference);
    double eloOther = otherStats.getElo(ladder);
    Assertions.assertEquals(base + winnersDifference, eloOther);
    staleStats.increaseElo(ladder, winnersDifference);
    double eloStale = staleStats.getElo(ladder);
    Assertions.assertEquals(base + (winnersDifference * 2), eloStale);
  }

  @Test
  public void testLeaderboard() {
    GuidoTestRuntime runtime = GuidoTestRuntime.createRuntime();
    String context = "junit";
    String stat = "dummy";
    // int players = 10;
    // for (int i = 0; i < players; i++) {
    //  MinecraftLinkable linkable = runtime.createMinecraftLinkable("Player" + i, false);
    //  Stats stats = runtime.getLoader().getStats().getForMinecraftLink(linkable, context);
    //  stats.increaseValue(stat, i * 10);
    // }
    Map<Integer, ? extends LeaderboardEntry> leaderboard =
        runtime.getLoader().getStats().getLeaderboard(context, stat, 0, 10);
    System.out.println(leaderboard);
  }
}
