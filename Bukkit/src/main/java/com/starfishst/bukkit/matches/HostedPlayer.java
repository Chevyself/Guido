package com.starfishst.bukkit.matches;

import com.starfishst.bukkit.client.requests.BukkitRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Stateable;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.maps.Maps;
import me.googas.messaging.api.MessengerListenFailException;

/** This is basically a minecraft linkable information */
public class HostedPlayer implements Stateable {

  @NonNull @Getter private final SimpleValuesMap identification;

  @NonNull @Getter private final SimpleValuesMap recognition;

  @NonNull @Getter private final Map<String, Float> stats;

  public HostedPlayer(
      @NonNull SimpleValuesMap identification,
      @NonNull SimpleValuesMap recognition,
      @NonNull Map<String, Float> stats) {
    this.identification = identification;
    this.recognition = recognition;
    this.stats = stats;
  }

  /** @deprecated this may only be used by gson */
  public HostedPlayer() {
    this(new SimpleValuesMap(), new SimpleValuesMap(), new HashMap<>());
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

  @Nullable
  public static HostedPlayer parse(@NonNull LinkableInfo link) throws MessengerListenFailException {
    return new BukkitRequest<>(
            HostedPlayer.class,
            "link-identification",
            Maps.objects("type", LinkableType.MINECRAFT)
                .append("identification", link.getIdentification()))
        .send();
  }

  @NonNull
  public LinkableInfo toLink() {
    return new SimpleLinkableInfo(LinkableType.MINECRAFT, this.identification);
  }

  @NonNull
  public UUID getUniqueId() {
    return UUIDUtils.untrim(this.identification.get("uuid", String.class));
  }

  @NonNull
  public String getNickname() {
    return this.recognition.getOr("nickname", String.class, "");
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("identification", this.identification)
        .append("recognition", this.recognition)
        .build();
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
