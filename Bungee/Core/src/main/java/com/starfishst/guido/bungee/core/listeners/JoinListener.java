package com.starfishst.guido.bungee.core.listeners;

import com.starfishst.core.utils.cache.Cache;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.UnlinkedMemberData;
import com.starfishst.guido.api.implementations.messaging.json.requests.data.MemberDataRequest;
import com.starfishst.guido.bungee.api.Guido;
import com.starfishst.guido.bungee.api.events.GuidoListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/** Listens for the joining users to give them */
public class JoinListener implements GuidoListener {

  public JoinListener() {
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
      String uniqueId = player.getUniqueId().toString();
      MemberData data =
          Cache.getCatchable(
              catchable ->
                  catchable instanceof MemberData
                          && ((MemberData) catchable).getLinks().get("minecraft").equals(uniqueId)
                      || catchable instanceof UnlinkedMemberData
                          && ((UnlinkedMemberData) catchable).getKey().equals("minecraft")
                          && ((UnlinkedMemberData) catchable).getValue().equals(uniqueId),
              MemberData.class);
      if (data != null) {
        data.refresh();
      }
    }
  }

  /**
   * Listens for players joining the game and requests the member to be used in the implementation
   *
   * @param event the event of an user joining the game
   */
  @EventHandler(priority = 5)
  public void onProxyPlayerJoin(PostLoginEvent event) {
    String uniqueId = event.getPlayer().getUniqueId().toString();
    Guido.request(
        new MemberDataRequest(
            Guido.getConfiguration().getGuildId(), "minecraft", uniqueId, false, true),
        catchable ->
            catchable instanceof MemberData
                    && ((MemberData) catchable).getLinks().get("minecraft").equals(uniqueId)
                || catchable instanceof UnlinkedMemberData
                    && ((UnlinkedMemberData) catchable).getKey().equals("minecraft")
                    && ((UnlinkedMemberData) catchable).getValue().equals(uniqueId),
        member -> {
          PermissionStack bungee = member.getPermissions("bungee");
          if (bungee != null) {
            for (Permission permission : bungee.getPermissions()) {
              event.getPlayer().setPermission(permission.getNode(), permission.isEnabled());
            }
          }
        });
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "join";
  }
}
