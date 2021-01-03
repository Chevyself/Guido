package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.core.client.requests.BungeeRequest;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.core.utility.Chat;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.annotations.Settings;
import com.starfishst.core.objects.JoinedStrings;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands created for punishments */
public class PunishmentCommands {

  @Settings("async")
  @Command(aliases = "ban", permission = "guido.ban")
  public void ban(
      CommandSender sender,
      BungeeLocaleFile locale,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Required(name = "Expires", description = "When does the punishment expire") Time expires,
      @Multiple @Required(name = "Reason", description = "The reason to why the player is banned")
          JoinedStrings reason) {
    this.punish(PunishmentType.BAN, sender, player, reason.build(), expires)
        .sendIfPresent(
            punishment -> {
              ProxiedPlayer proxy = player.toProxy();
              sender.sendMessage(
                  locale.getComponent(
                      "ban.success", Maps.singleton("nickname", player.getNickname())));
              if (proxy == null) return;
              proxy.disconnect(
                  Chat.getLocale(proxy)
                      .getComponent("server.banned", Maps.singleton("reason", reason.build())));
            });
  }

  @Settings("async")
  @Command(
      aliases = {"warn", "w"},
      permission = "guido.warn")
  public void warn(
      CommandSender sender,
      BungeeLocaleFile locale,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Multiple @Required(name = "Reason", description = "The reason to why the player is banned")
          JoinedStrings reason) {
    this.punish(PunishmentType.WARN, sender, player, reason.build(), new Time(1, Unit.WEEKS))
        .sendIfPresent(
            present -> {
              ProxiedPlayer proxy = player.toProxy();
              sender.sendMessage(
                  locale.getComponent(
                      "warn.success", Maps.singleton("nickname", player.getNickname())));
              if (proxy == null) return;
              proxy.disconnect(
                  Chat.getLocale(proxy)
                      .getComponent("server.warn", Maps.singleton("reason", reason.build())));
            });
  }

  @Settings("async")
  @Command(aliases = "kick", permission = "guido.kick")
  public void kick(
      CommandSender sender,
      BungeeLocaleFile locale,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Multiple @Required(name = "Reason", description = "The reason to why the player is banned")
          JoinedStrings reason) {
    this.punish(PunishmentType.KICK, sender, player, reason.build(), new Time(1, Unit.MONTHS))
        .sendIfPresent(
            punishment -> {
              ProxiedPlayer proxy = player.toProxy();
              sender.sendMessage(
                  locale.getComponent(
                      "kick.success", Maps.singleton("nickname", player.getNickname())));
              if (proxy == null) return;
              proxy.disconnect(
                  Chat.getLocale(proxy)
                      .getComponent("server.kicked", Maps.singleton("reason", reason.build())));
            });
  }

  @NonNull
  private BungeeRequest<Punishment> punish(
      @NonNull PunishmentType type,
      @NonNull CommandSender sender,
      @NonNull ProxiedOfflinePlayer punished,
      @Nullable String reason,
      @NonNull Time expires) {
    return new BungeeRequest<>(
        Punishment.class,
        "punishment/create",
        Maps.objects("type", type)
            .append("status", PunishmentStatus.ACTIVE)
            .append(
                "punisher",
                sender instanceof ProxiedPlayer
                    ? new ProxiedOfflinePlayer((ProxiedPlayer) sender).getLinkedInfo()
                    : null)
            .append("punished", punished.getLinkedInfo())
            .append(
                "details",
                new SimpleValuesMap("reason", reason == null ? "No reason provided" : reason))
            .append(
                "expires",
                expires.millis() == 0 ? -1 : System.currentTimeMillis() + expires.millis()));
  }
}
