package me.googas.bot.core.matches.queue;

import java.util.*;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.matches.queue.Queueable;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.user.UserData;
import me.googas.api.utility.Lots;
import me.googas.bot.api.Guido;
import me.googas.bot.core.util.Lang;
import me.googas.net.api.exception.MessengerListenFailException;
import me.googas.net.sockets.json.server.JsonClientThread;

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
  public AbstractMatch checkReady() {
    Ladder ladder = this.getLadder();
    // TODO if there is more than 30 people playing create a queue based on elo
    // to create this we should create playlist to count people playing on it not people in queue
    if (this.getWaiting().size() >= ladder.playersPerTeam() * 2) {
      Set<TeamMember> participants = new HashSet<>();
      for (int i = 0; i < ladder.playersPerTeam() * 2; i++) {
        Queueable queueable = this.getWaiting().get(i);
        if (!(queueable instanceof LinkableInfo)) continue;
        participants.add(new TeamMember((LinkableInfo) queueable, TeamRole.NORMAL));
      }
      for (TeamMember participant : participants) {
        this.getWaiting().remove(participant.getLink());
      }
      Map<String, Map<String, Object>> information = new HashMap<>();
      LinkedHashMap<String, Object> global = new LinkedHashMap<>();
      global.put("type", "pgm");
      global.put("ladder", ladder.getName());
      information.put("global", global);
      return new AbstractMatch(
              information,
              Lots.set(new MatchTeam(-2, participants, "participants")),
              MatchStatus.WAITING,
              null)
          .cache();
    }
    return null;
  }

  @Override
  public QueueResult join(@NonNull Queueable queueable) {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().get().getBungee();
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
        Objects.requireNonNull(link.toMinecraftRef(), "Does not have a linked minecraft account");
    try {
      if (Requests.Bungee.isOnline(toPlay.getUuid()).send(bungee).orElse(false)) {
        QueueResult join = super.join(toPlay.getInfo());
        if (join.isCancelled()) return join;
        Requests.Bungee.addQueue(toPlay.getUuid()).queue(bungee);
        return new QueueResult();
      } else {
        return new QueueResult(locale.get("pgm-queue.join-server"));
      }
    } catch (MessengerListenFailException e) {
      return new QueueResult(locale.get("pgm-queue.no-bungee"));
    }
  }

  @Override
  public QueueResult leave(@NonNull Queueable queueable) {
    QueueResult leave = super.leave(queueable);
    if (leave.isCancelled()) return leave;
    if (!(queueable instanceof LinkableInfo)) return new QueueResult("Queueable must be a link");
    JsonClientThread bungee = Guido.getServer().getAuthenticator().get().getBungee();
    Linkable linked = ((LinkableInfo) queueable).getLink();
    if (bungee != null && linked != null && linked.getType() == LinkableType.MINECRAFT) {
      Requests.Bungee.removeQueue(linked.requireMinecraftRef().getUuid()).queue(bungee);
    }
    return new QueueResult();
  }
}
