package me.googas.bot.core.matches.queue;

import java.util.*;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.user.UserData;
import me.googas.api.utility.Lots;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.util.Lang;
import me.googas.net.api.exception.MessengerListenFailException;
import org.jetbrains.annotations.NotNull;

/** A queue that uses pgm */
public class GuidoPGMQueue extends GuidoQueue {

  public GuidoPGMQueue(@NonNull String ladder, @NonNull GuidoBotRuntime runtime) {
    super(ladder, runtime);
  }

  @NotNull
  @Override
  public Optional<MinecraftMatch> checkReady() {
    Ladder ladder = this.getLadder();
    MinecraftMatch match = null;
    // TODO if there is more than 30 people playing create a queue based on elo
    // to create this we should create playlist to count people playing on it not people in queue
    if (this.getWaiting().size() >= ladder.playersPerTeam() * 2) {
      Set<ImmutableMinecraftTeamMember> participants = new HashSet<>();
      for (int i = 0; i < ladder.playersPerTeam() * 2; i++) {
        MinecraftLinkable linkable = this.getWaiting().remove(i);
        participants.add(new ImmutableMinecraftTeamMember(linkable.getId(), TeamRole.MEMBER));
      }
      match =
          runtime
              .getLoader()
              .getMinecraftMatches()
              .createMatch(
                  Lots.set(new ImmutableMinecraftMatchTeam(-2, participants, "participants")),
                  ladder.getName());
    }
    return Optional.ofNullable(match);
  }

  private boolean isOnline(UUID uuid) {
    return Guido.getAuthenticator()
        .getBungee()
        .map(
            bungee -> {
              try {
                return Requests.Bungee.isOnline(uuid).send(bungee).orElse(false);
              } catch (MessengerListenFailException e) {
                return true;
              }
            })
        .orElse(true);
  }

  @Override
  public @NonNull QueueResult leave(@NonNull MinecraftLinkable minecraft) {
    QueueResult leave = super.leave(minecraft);
    if (leave.isCancelled()) return leave;
    Guido.getAuthenticator()
        .getBungee()
        .ifPresent(bungee -> Requests.Bungee.removeQueue(minecraft.getId()).queue(bungee));
    return new QueueResult();
  }

  @Override
  public @NonNull QueueResult join(@NonNull MinecraftLinkable minecraft) {
    LocaleFile locale = Lang.getLocale(minecraft);
    Optional<UserData> optional =
        minecraft
            .getLinkedUserId()
            .flatMap(linkedUserId -> runtime.getLoader().getUsers().getById(linkedUserId));
    if (optional.isEmpty()) return new QueueResult(locale.get("pgm-queue.link-first"));
    if (isOnline(minecraft.getId())) {
      QueueResult join = super.join(minecraft);
      if (join.isCancelled()) return join;
      Guido.getAuthenticator()
          .getBungee()
          .ifPresent(bungee -> Requests.Bungee.addQueue(minecraft.getId()).queue(bungee));
      return new QueueResult();
    } else {
      return new QueueResult(locale.get("pgm-queue.join-server"));
    }
  }
}
