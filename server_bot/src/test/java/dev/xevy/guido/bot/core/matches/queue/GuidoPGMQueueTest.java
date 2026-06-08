package dev.xevy.guido.bot.core.matches.queue;

import dev.xevy.guido.bot.GuidoTestRuntime;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import me.googas.api.Requests;
import me.googas.api.immutable.ImmutableMinecraftMatch;
import me.googas.api.immutable.ImmutableMinecraftMatchTeamMember;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.MinecraftQueue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.core.handlers.queue.QueueHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GuidoPGMQueueTest {

  @Test
  public void testReadyWithClientCall() throws IOException {
    GuidoTestRuntime runtime = GuidoTestRuntime.createRuntime();
    AtomicReference<ImmutableMinecraftMatch> canHostMatch = new AtomicReference<>(null);
    AtomicReference<ImmutableMinecraftMatch> hostMatch = new AtomicReference<>(null);
    runtime.joinWithClient(
        runtime.listen(
            Requests.MatchServer.CAN_HOST,
            (context) -> {
              canHostMatch.set(
                  context.get(Requests.MatchServer.CAN_HOST_MATCH, ImmutableMinecraftMatch.class));
              return true;
            }),
        runtime.listen(
            Requests.MatchServer.HOST,
            (context) -> {
              hostMatch.set(
                  context.get(Requests.MatchServer.HOST_MATCH, ImmutableMinecraftMatch.class));
              return "server-ip";
            }));
    Ladder ladder =
        runtime
            .getJdaProvider()
            .getGuidoGuild()
            .addLadder("1v1", 1, 500, 2, 1f, 1f, MinecraftTeamSelectionType.RANDOM)
            .orElseThrow();
    MinecraftQueue queue = runtime.getHandlers().getHandler(QueueHandler.class).createQueue(ladder);
    MinecraftLinkable linkableA = runtime.createMinecraftLinkable("A", true);
    MinecraftLinkable linkableB = runtime.createMinecraftLinkable("B", true);
    QueueResult joinA = queue.join(linkableA);
    QueueResult joinB = queue.join(linkableB);
    Assertions.assertFalse(
        joinA.isCancelled(),
        "Join for linkableA must not have been cancelled: " + joinA.getReason());
    Assertions.assertFalse(
        joinB.isCancelled(),
        "Join for linkableB must not have been cancelled: " + joinB.getReason());
    Assertions.assertEquals(canHostMatch.get(), hostMatch.get());
    ImmutableCollection<ImmutableMinecraftMatchTeamMember> canHostParticipants =
        canHostMatch.get().getParticipants();
    boolean isAInCanHost =
        canHostParticipants.stream().anyMatch(member -> member.getId().equals(linkableA.getId()));
    boolean isBInCanHost =
        canHostParticipants.stream().anyMatch(member -> member.getId().equals(linkableA.getId()));
    Assertions.assertTrue(isAInCanHost, "linkableA must be in match");
    Assertions.assertTrue(isBInCanHost, "linkableB must be in match");
  }
}
