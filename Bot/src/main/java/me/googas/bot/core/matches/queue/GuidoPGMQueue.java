package me.googas.bot.core.matches.queue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.matches.queue.Queueable;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.user.UserData;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.bot.core.matches.GuidoMatch;
import me.googas.bot.core.matches.GuidoMatchTeam;
import me.googas.bot.core.matches.team.GuidoTeamMember;
import me.googas.bot.core.util.Lang;
import me.googas.commons.Lots;
import me.googas.commons.Validate;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.server.JsonClientThread;

/** A queue that uses pgm */
public class GuidoPGMQueue extends GuidoQueue {

  /**
   * Create the queue
   *
   * @param guildId the id of the guild where the queue is happening
   * @param ladder the names of the ladders waiting for queue
   */
  public GuidoPGMQueue(long guildId, @NonNull String ladder) {
    super(guildId, ladder);
  }

  @Override
  public GuidoMatch checkReady() {
    Ladder ladder = this.getLadder();
    // TODO if there is more than 30 people playing create a queue based on elo
    // to create this we should create playlist to count people playing on it not people in queue
    if (this.getWaiting().size() >= ladder.playersPerTeam() * 2) {
      Set<TeamMember> participants = new HashSet<>();
      for (int i = 0; i < ladder.playersPerTeam() * 2; i++) {
        Queueable queueable = this.getWaiting().get(i);
        if (!(queueable instanceof LinkableInfo)) continue;
        participants.add(new GuidoTeamMember((LinkableInfo) queueable, TeamRole.NORMAL));
      }
      for (TeamMember participant : participants) {
        this.getWaiting().remove(participant.getLinkInfo());
      }
      return new GuidoMatch(
              this.getGuildId(),
              Lots.set(new GuidoMatchTeam(-2, participants, "participants")),
              new GuidoLinkedValuesMap("type", "pgm").put("ladder", ladder.getName()))
          .cache();
    }
    return null;
  }

  @Override
  public QueueResult join(@NonNull Queueable queueable) {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    LocaleFile locale = Lang.getLocale(queueable);
    if (bungee == null) return new QueueResult(locale.get("pgm-queue.no-bungee"));
    if (!(queueable instanceof LinkableInfo))
      // TODO probably in the future when team queue is available
      return new QueueResult("Queueable must be a link");
    Linkable data = ((LinkableInfo) queueable).getLink();
    if (data == null) return new QueueResult(locale.get("pgm-queue.internal"));
    UserData user = data.getLinkedUser();
    if (user == null) return new QueueResult(locale.get("pgm-queue.link-first"));
    Linkable link = user.getLink(LinkableType.MINECRAFT);
    if (link == null) return new QueueResult(locale.get("pgm-queue.link-first"));
    MinecraftLinkable toPlay =
        Validate.notNull(link.toMinecraftRef(), "Does not have a linked minecraft account");
    UUID uuid = toPlay.getUuid();
    try {
      Boolean bol =
          bungee.sendRequest(
              new Request<>(Boolean.class, "bungee/is-online", Maps.singleton("uuid", uuid)));
      if (bol != null) {
        if (bol) {
          QueueResult join = super.join(queueable);
          if (join.isCancelled()) return join;
          bungee.sendRequest(
              new Request<>(Boolean.class, "bungee/add-queue", Maps.singleton("uuid", uuid)),
              ignored -> {});
        } else {
          return new QueueResult(locale.get("pgm-queue.join-server"));
        }
      }
      return new QueueResult(locale.get("pgm-queue.internal-not-check"));
    } catch (MessengerListenFailException e) {
      return new QueueResult(e.getMessage());
    }
  }

  @Override
  public QueueResult leave(@NonNull Queueable queueable) {
    QueueResult leave = super.leave(queueable);
    if (leave.isCancelled()) return leave;
    if (!(queueable instanceof LinkableInfo)) return new QueueResult("Queueable must be a link");
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    Linkable linked = ((LinkableInfo) queueable).getLink();
    if (bungee != null && linked != null && linked.getType() == LinkableType.MINECRAFT) {
      bungee.sendRequest(
          new Request<>(
              Boolean.class,
              "remove-queue",
              Maps.singleton("uuid", new MinecraftLinkable(linked).getUuid())),
          bol -> {});
    }
    return new QueueResult();
  }
}
