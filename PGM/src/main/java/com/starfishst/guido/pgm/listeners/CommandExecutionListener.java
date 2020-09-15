package com.starfishst.guido.pgm.listeners;

import com.starfishst.bukkit.utils.BukkitUtils;
import com.starfishst.core.utils.Strings;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.pgm.api.events.GuidoListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

/** Listens for the commands that are being executed by other players */
public class CommandExecutionListener implements GuidoListener {

  /**
   * When the event {@link PlayerCommandPreprocessEvent} is called this will send the message for
   * the people with certain permission
   *
   * @param event the event of the command pre process
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
    Player player = event.getPlayer();
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (onlinePlayer.hasPermission(
          this.getSettings().getSettingOr("permission", String.class, "guido.adminchat"))) {
        String msg =
            BukkitUtils.build(
                this.getSettings()
                    .getSettingOr(
                        "chat", String.class, "[&6A&r] %player_display% &6executed &r%command%"));
        onlinePlayer.sendMessage(
            Strings.buildMessage(
                msg,
                Maps.builder("player", player.getName())
                    .append("player_display", player.getDisplayName())
                    .append("command", event.getMessage())));
      }
    }
  }

  @Override
  public @NotNull String getName() {
    return "command-execution";
  }

  @Override
  public void onUnload() {}
}
