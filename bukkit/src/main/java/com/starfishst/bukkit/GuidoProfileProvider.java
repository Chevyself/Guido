package com.starfishst.bukkit;

import java.util.HashMap;
import java.util.UUID;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import me.googas.commons.cache.MemoryCache;
import me.googas.commons.maps.Maps;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.starbox.modules.data.PlayersHandler;
import me.googas.starbox.modules.data.type.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class GuidoProfileProvider implements PlayersHandler.ProfileProvider {

  // TODO add the cache to a bukkit task
  @NonNull private final MemoryCache playerCache = new MemoryCache();

  public GuidoProfileProvider startTask() {
    Bukkit.getScheduler().runTaskTimerAsynchronously(Guido.getPlugin(), this.playerCache, 0, 20);
    return this;
  }

  @Nullable
  @Override
  public Profile getPlayer(@NonNull String name) {
    return this.playerCache.getOrSupply(
        Profile.class,
        loadedProfile -> loadedProfile.getName().equalsIgnoreCase(name),
        () -> {
          try {
            Linkable linkable =
                Requests.Links.getLink(
                        LinkableType.MINECRAFT, new HashMap<>(), Maps.singleton("nickname", name))
                    .send(Guido.getClient().getConnection());
            if (linkable != null) {
              GuidoProfile profile = new GuidoProfile(linkable);
              this.playerCache.add(profile);
              return profile;
            }
          } catch (MessengerListenFailException e) {
            e.printStackTrace();
          }
          return null;
        });
  }

  @Override
  public @NonNull Profile getPlayer(@NonNull OfflinePlayer offlinePlayer) {
    return this.playerCache.getOrSupply(
        Profile.class,
        linkable -> linkable.getUniqueId().equals(offlinePlayer.getUniqueId()),
        () -> {
          try {
            Linkable linkable =
                Requests.Links.getLink(
                        LinkableType.MINECRAFT,
                        Maps.singleton("uuid", UUIDUtils.trim(offlinePlayer.getUniqueId())),
                        new HashMap<>())
                    .send(Guido.getClient().getConnection());
            if (linkable != null) {
              GuidoProfile profile = new GuidoProfile(linkable);
              this.playerCache.add(profile);
              return profile;
            }
          } catch (MessengerListenFailException e) {
            e.printStackTrace();
          }
          return null;
        });
  }

  @Nullable
  @Override
  public Profile getPlayer(@NonNull UUID uuid) {
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
    if (offlinePlayer == null) return null;
    return this.getPlayer(offlinePlayer);
  }
}
