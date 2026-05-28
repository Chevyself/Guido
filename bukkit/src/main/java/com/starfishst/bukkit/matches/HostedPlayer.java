package com.starfishst.bukkit.matches;

import com.starfishst.bukkit.Guido;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.Identifiable;
import me.googas.api.Requests;
import me.googas.api.Stateable;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.net.api.exception.MessengerListenFailException;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.UUIDUtils;

/** This is basically a minecraft linkable information */
public class HostedPlayer implements Stateable, Identifiable {

  @NonNull @Getter private final Map<String, Object> identification;

  @NonNull @Getter private final Map<String, Object> recognition;

  @NonNull @Getter private final Map<String, Map<String, Double>> stats;

  public HostedPlayer(
      @NonNull Map<String, Object> identification,
      @NonNull Map<String, Object> recognition,
      @NonNull Map<String, Map<String, Double>> stats) {
    this.identification = identification;
    this.recognition = recognition;
    this.stats = stats;
  }

  /** @deprecated this may only be used by gson */
  public HostedPlayer() {
    this(new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  public HostedPlayer(@NonNull Linkable linkable) {
    this(linkable.getIdentification(), linkable.getRecognition(), linkable.getStats());
  }

  @NonNull
  public static Set<HostedPlayer> parse(@NonNull Collection<LinkableInfo> links) {
    Set<HostedPlayer> players = new HashSet<>();
    if (links.isEmpty()) return players;
    for (LinkableInfo link : links) {
      try {
        players.add(HostedPlayer.parse(link));
      } catch (MessengerListenFailException e) {
        e.printStackTrace();
      }
    }
    return players;
  }

  public static HostedPlayer parse(@NonNull LinkableInfo link) throws MessengerListenFailException {
    JsonClient connection = Guido.getClient().getConnection();
    if (connection == null) return null;
    Optional<Linkable> optional =
        Requests.Links.getLinkByIdentification(LinkableType.MINECRAFT, link.getIdentification())
            .send(connection);
    return optional.map(HostedPlayer::new).orElse(null);
  }

  @NonNull
  public static Set<LinkableInfo> toInfo(@NonNull Collection<HostedPlayer> players) {
    Set<LinkableInfo> links = new HashSet<>();
    for (HostedPlayer player : players) {
      links.add(player.toLink());
    }
    return links;
  }

  @NonNull
  public LinkableInfo toLink() {
    return new LinkableInfo(LinkableType.MINECRAFT, this.identification, new HashMap<>());
  }

  @NonNull
  public UUID getUniqueId() {
    return UUIDUtils.untrim(this.getIdString("uuid", ""));
  }

  @NonNull
  public String getNickname() {
    return this.getRecogString("nickname", "");
  }

  @Override
  public String toString() {
    return "HostedPlayer{"
        + "identification="
        + identification
        + ", recognition="
        + recognition
        + ", stats="
        + stats
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    HostedPlayer that = (HostedPlayer) o;
    return this.identification.equals(that.identification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.identification);
  }
}
