package com.starfishst.bukkit;

import com.starfishst.bukkit.api.Guido;
import java.util.HashMap;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import me.googas.commons.Validate;
import me.googas.commons.cache.MemoryCache;
import me.googas.commons.maps.Maps;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.starbox.modules.data.PlayersHandler;
import me.googas.starbox.modules.data.type.Profile;
import org.bukkit.OfflinePlayer;

public class GuidoProfileProvider implements PlayersHandler.ProfileProvider {

  // TODO add the cache to a bukkit task
  @NonNull private final MemoryCache playerCache = new MemoryCache();

  @Nullable
  @Override
  public Profile getPlayer(@NonNull String name) {
    Linkable supply =
        this.playerCache.getOrSupply(
            Linkable.class,
            linkable -> linkable.getRecogString("nickname", "").equalsIgnoreCase(name),
            () -> {
              try {
                Linkable linkable =
                    Requests.Links.getLink(
                            LinkableType.MINECRAFT,
                            new HashMap<>(),
                            Maps.singleton("nickname", name))
                        .send(Guido.getClient().getConnection());
                if (linkable != null) this.playerCache.add(linkable);
                return linkable;
              } catch (MessengerListenFailException e) {
                e.printStackTrace();
              }
              return null;
            });
    return new GuidoProfile(Validate.notNull(supply, "Could not supply linkable of " + name));
  }

  @Override
  public @NonNull Profile getPlayer(OfflinePlayer offlinePlayer) {
    Linkable supply =
        this.playerCache.getOrSupply(
            Linkable.class,
            linkable -> linkable.getIdUUID("uuid", true).equals(offlinePlayer.getUniqueId()),
            () -> {
              try {
                Linkable linkable =
                    Requests.Links.getLink(
                            LinkableType.MINECRAFT,
                            Maps.singleton("uuid", UUIDUtils.trim(offlinePlayer.getUniqueId())),
                            new HashMap<>())
                        .send(Guido.getClient().getConnection());
                if (linkable != null) this.playerCache.add(linkable);
                return linkable;
              } catch (MessengerListenFailException e) {
                e.printStackTrace();
              }
              return null;
            });
    return new GuidoProfile(
        Validate.notNull(supply, "Could not supply linkable of " + offlinePlayer));
  }
}
