package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.UUIDUtils;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PermissionsListener implements GuidoListener {

  /**
   * Listen for a player joining the game to give it permissions
   *
   * @param event the event of a player joining the game
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPostLoginEvent(PostLoginEvent event) {
    JsonClient connection = Guido.getClient().getConnection();
    if (connection == null) return;
    ProxiedPlayer player = event.getPlayer();
    String trim = UUIDUtils.trim(player.getUniqueId());
    LinkableInfo link =
        new SimpleLinkableInfo(LinkableType.MINECRAFT, new SimpleValuesMap("uuid", trim));
    Requests.Links.permissions(link, "bungee", true)
        .send(
            connection,
            optional -> {
              optional.ifPresent(
                  stack -> {
                    this.add(player, stack.getPermissions());
                    List<Group> groups = this.groups().getGroups(player, true);
                    groups.addAll(
                        this.groups()
                            .getParents(
                                groups)); // TODO probably add this to the same method as above
                    for (Group group : groups) {
                      PermissionStack bungee = group.getPermissions("bungee");
                      if (bungee != null) this.add(player, bungee.getPermissions());
                      PermissionStack global = group.getPermissions("global");
                      if (global != null) this.add(player, global.getPermissions());
                    }
                  });
            });
  }

  public void add(@NonNull ProxiedPlayer player, @Nullable Permission permission) {
    if (permission == null || permission.isExpired()) return;
    player.setPermission(permission.getNode(), permission.isEnabled());
  }

  public void add(@NonNull ProxiedPlayer player, @Nullable Collection<Permission> permissions) {
    if (permissions == null) return;
    for (Permission permission : permissions) {
      this.add(player, permission);
    }
  }

  @NonNull
  private GroupListener groups() {
    return Guido.getListener(GroupListener.class);
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "permissions";
  }
}
