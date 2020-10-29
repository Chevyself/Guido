package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import me.googas.api.Permission;
import me.googas.api.client.data.LinkedInfoImpl;
import me.googas.api.client.data.PermissionStackImpl;
import me.googas.api.client.data.ValuesMapImpl;
import me.googas.api.links.LinkedDataType;
import java.io.IOException;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/** Listens for the joining users to give them */
public class JoinListener implements GuidoListener {

  /**
   * Listens for players joining the game and requests the member to be used in the implementation
   *
   * @param event the event of an user joining the game
   */
  @EventHandler(priority = 5)
  public void onProxyPlayerJoin(PostLoginEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    try {
      JsonClient connection = Guido.getClient().validatedConnection();
      LinkedInfoImpl info =
          new LinkedInfoImpl(
              LinkedDataType.MINECRAFT,
              new ValuesMapImpl(Maps.singleton("uuid", UUIDUtils.trim(uuid))));
      connection.sendRequest(
          new Request<>(Boolean.class, "data-exists", Maps.singleton("info", info)),
          exists -> {
            if (exists) {
              connection.sendRequest(
                  new Request<>(
                      PermissionStackImpl.class,
                      "permission",
                      Maps.objects("info", info).append("context", "bungee").build()),
                  stack -> {
                    for (Permission permission : stack.getPermissions()) {
                      event.getPlayer().setPermission(permission.getNode(), permission.isEnabled());
                    }
                  });
              // TODO update name
            } else {
              connection.sendRequest(
                  new Request<>(
                      Boolean.class,
                      "create-minecraft",
                      Maps.objects("uuid", uuid)
                          .append("nickname", event.getPlayer().getName())
                          .build()),
                  ignored -> {});
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
