package com.starfishst.bukkit.handlers;

import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/** Listens for the commands that are being executed by other players */
public class CommandExecutionHandler implements Handler {

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
          this.getSettings().getOr("permission", String.class, "guido.adminchat"))) {
        String msg =
            BukkitUtils.build(
                this.getSettings()
                    .getOr(
                        "chat", String.class, "[&6A&r] %player_display% &6executed &r%command%"));
        onlinePlayer.sendMessage(
            Strings.build(
                msg,
                Maps.builder("player", player.getName())
                    .append("player_display", player.getDisplayName())
                    .append("command", event.getMessage())));
      }
    }
  }

  /**
   * Check whether the name of the command is banned
   *
   * @param commandName the name of the command to check if it is banned
   * @return if the name of the command is contained inside {@link #getBanned()} it will return true
   */
  public boolean isBanned(@NonNull String commandName) {
    for (String bannedName : this.getBanned()) {
      if (bannedName.equalsIgnoreCase(commandName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the list of banned commands. The banned commands are the ones that this listener will
   * ignore from printing
   *
   * @return the list of banned commands
   */
  @NonNull
  public List<String> getBanned() {
    return this.getSettings().getListValue("banned");
  }

  @Override
  public @NonNull String getName() {
    return "command-execution";
  }

  @Override
  public void onDisable() {}
}
