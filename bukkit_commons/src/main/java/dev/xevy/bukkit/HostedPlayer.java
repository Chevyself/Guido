package dev.xevy.bukkit;

import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.utility.ImmutableCollection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/** This is basically a minecraft linkable information */
public class HostedPlayer {

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
    if (player != null) return player.getName();
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
    if (offlinePlayer != null) return offlinePlayer.getName();
    return "Offline";
  }

  public static Set<HostedPlayer> parse(
      @NonNull ImmutableCollection<? extends MinecraftMatchTeamMember> participants) {
    return participants.stream()
        .map(participant -> new HostedPlayer(participant.getId(), new HashMap<>()))
        .collect(Collectors.toSet());
  }
}
