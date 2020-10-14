package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.List;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
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
    if (this.isBanned(event.getMessage().split(" ")[0].replace("/", ""))) return;
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

  public boolean isBanned(@NotNull String commandName) {
    for (String bannedName : this.getBanned()) {
      if (bannedName.equalsIgnoreCase(commandName)) {
        return true;
      }
    }
    return false;
  }

  public List<String> getBanned() {
    return this.getSettings().getListSetting("banned", String.class);
  }

  @Override
  public @NotNull String getName() {
    return "command-execution";
  }

  @Override
  public void onUnload() {}
}
