package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import me.googas.api.client.data.LinkableInfoImpl;
import me.googas.api.client.data.PermissionStackImpl;
import me.googas.api.client.data.ValuesMapImpl;
import me.googas.api.links.LinkableDataType;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for the joining users to give them
 *
 * <p>TODO needs refactoring
 */
public class JoinListener implements GuidoListener {

  /**
   * Listens for players joining the game and requests the member to be used in the implementation
   *
   * @param event the event of an user joining the game
   */
  @EventHandler(priority = 5)
  public void onProxyPlayerJoin(PostLoginEvent event) {
    ProxiedPlayer player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    try {
      JsonClient connection = Guido.getClient().validatedConnection();
      LinkableInfoImpl info =
          new LinkableInfoImpl(
              LinkableDataType.MINECRAFT,
              new ValuesMapImpl(Maps.singleton("uuid", UUIDUtils.trim(uuid))));
      connection.sendRequest(
          new Request<>(Boolean.class, "data-exists", Maps.singleton("info", info)),
          exists -> {
            Guido.getLogger().info("The data for " + player.getName() + " exists? " + exists);
            if (exists) {
              connection.sendRequest(
                  new Request<>(
                      PermissionStackImpl.class,
                      "permission",
                      Maps.objects("info", info).append("context", "bungee").build()),
                  stack -> {
                    GroupListener groupsListener = Guido.getListener(GroupListener.class);
                    List<Group> toGive = new ArrayList<>();
                    for (Permission permission : stack.getPermissions()) {
                      if (permission.getNode().startsWith("guido.group.")) {
                        Group group = groupsListener.getGroupByPermission(permission.getNode());
                        if (group != null) {
                          toGive.add(group);
                        } else {
                          player.setPermission(permission.getNode(), permission.isEnabled());
                        }
                      } else {
                        player.setPermission(permission.getNode(), permission.isEnabled());
                      }
                    }
                    toGive.sort(Comparator.comparingInt(Group::getWeight));
                    Collections.reverse(toGive);
                    for (Group group : toGive) {
                      PermissionStack bungee = group.getPermissions("bungee");
                      if (bungee != null) {
                        for (Permission permission : bungee.getPermissions()) {
                          player.setPermission(permission.getNode(), permission.isEnabled());
                        }
                      }
                    }
                  });
              connection.sendRequest(
                  new Request<>(
                      Boolean.class,
                      "update-minecraft-nickname",
                      Maps.objects("uuid", uuid).append("nickname", player.getName()).build()),
                  updated -> {
                    Guido.getLogger()
                        .info("Updated mineraft nickname for? " + player + " " + updated);
                  });
            } else {
              connection.sendRequest(
                  new Request<>(
                      Boolean.class,
                      "create-minecraft",
                      Maps.objects("uuid", uuid).append("nickname", player.getName()).build()),
                  bol ->
                      Guido.getLogger()
                          .info(
                              "Attempting to create minecraft for "
                                  + player.getName()
                                  + " was it successful? "
                                  + bol));
            }
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "join";
  }
}
