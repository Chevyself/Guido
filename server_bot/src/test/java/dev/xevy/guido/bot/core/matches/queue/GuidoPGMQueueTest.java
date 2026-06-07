package dev.xevy.guido.bot.core.matches.queue;

import dev.xevy.guido.bot.GuidoTestRuntime;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.MinecraftQueue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.bot.core.handlers.queue.QueueHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GuidoPGMQueueTest {

  @Test
  public void testReadyWithClientCall() {
    GuidoTestRuntime runtime = GuidoTestRuntime.createRuntime();
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
    Assertions.assertFalse(joinA.isCancelled(), "Join for linkableA must not have been cancelled: " + joinA.getReason());
    Assertions.assertFalse(joinB.isCancelled(), "Join for linkableB must not have been cancelled: " + joinB.getReason());
  }
}
