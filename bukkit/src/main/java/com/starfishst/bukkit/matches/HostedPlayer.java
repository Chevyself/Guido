package com.starfishst.bukkit.matches;

import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.Stateable;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/** This is basically a minecraft linkable information */
public class HostedPlayer implements Stateable {

  @NonNull @Getter private final UUID id;
  @NonNull @Getter private final Map<String, Map<String, Double>> stats;

  public HostedPlayer(@NonNull UUID id, @NonNull Map<String, Map<String, Double>> stats) {
    this.id = id;
    this.stats = stats;
  }

  /** @deprecated this may only be used by gson */
  public HostedPlayer() {
    this(UUID.randomUUID(), new HashMap<>());
  }

  // TODO localize
  @NonNull
  public String getNickname() {
    Player player = Bukkit.getPlayer(id);
    return player == null ? "Offline" : player.getDisplayName();
  }

  public static Set<HostedPlayer> parse(@NonNull Set<MinecraftMatchTeamMember> participants) {
    return participants.stream()
        .map(participant -> new HostedPlayer(participant.getId(), new HashMap<>()))
        .collect(Collectors.toSet());
  }
}
