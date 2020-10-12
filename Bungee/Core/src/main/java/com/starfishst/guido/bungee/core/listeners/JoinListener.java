package com.starfishst.guido.bungee.core.listeners;

import com.starfishst.guido.bungee.api.events.GuidoListener;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/** Listens for the joining users to give them */
public class JoinListener implements GuidoListener {

  public JoinListener() {}

  /**
   * Listens for players joining the game and requests the member to be used in the implementation
   *
   * @param event the event of an user joining the game
   */
  @EventHandler(priority = 5)
  public void onProxyPlayerJoin(PostLoginEvent event) {}

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "join";
  }
}
