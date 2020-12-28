package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.client.data.SimpleLinkableInfo;
import me.googas.api.client.data.SimplePermissionStack;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.links.LinkableType;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * Listens for the joining users to give them
 *
 * <p>TODO needs refactoring
 */
public class JoinListener implements GuidoListener {

  @NonNull private final Set<UUID> joining = new HashSet<>();

  /**
   * Add them to the set of players pre login
   *
   * @param event the event of a player pre login
   */
  @EventHandler
  public void onPreJoinEvent(PreLoginEvent event) {
    this.joining.add(event.getConnection().getUniqueId());
  }

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
      SimpleLinkableInfo info =
          new SimpleLinkableInfo(
              LinkableType.MINECRAFT,
              new SimpleValuesMap(Maps.singleton("uuid", UUIDUtils.trim(uuid))));
      connection.sendRequest(
          new Request<>(Boolean.class, "data-exists", Maps.singleton("info", info)),
          exists -> {
            Guido.getLogger().info("The data for " + player.getName() + " exists? " + exists);
            if (exists) {
              connection.sendRequest(
                  new Request<>(
                      SimplePermissionStack.class,
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
                    ArrayList<Group> toGiveCopy = new ArrayList<>(toGive);
                    for (Group group : toGiveCopy) {
                      toGive.addAll(groupsListener.getParents(group));
                    }
                    toGive.sort(Comparator.comparingInt(Group::getWeight));
                    Collections.reverse(toGive);
                    for (Group group : toGive) {
                      PermissionStack bungee = group.getPermissions("bungee");
                      PermissionStack global = group.getPermissions("global");

                      if (bungee != null) {
                        for (Permission permission : bungee.getPermissions()) {
                          player.setPermission(permission.getNode(), permission.isEnabled());
                        }
                      }
                      if (global != null) {
                        for (Permission permission : global.getPermissions()) {
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
                  updated ->
                      Guido.getLogger()
                          .info("Updated mineraft nickname for? " + player + " " + updated));
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
    this.joining.remove(event.getPlayer().getUniqueId());
  }

  /**
   * Check whether a player is joining a server
   *
   * @param uuid the uuid of the player
   * @return true if the player is joining
   */
  public boolean isJoining(@NonNull UUID uuid) {
    return this.joining.contains(uuid);
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "join";
  }
}
