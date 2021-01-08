package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.core.utility.Chat;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.annotations.Settings;
import com.starfishst.core.objects.JoinedStrings;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.RequestBuilder;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands created for punishments */
public class PunishmentCommands {

  @Settings("async")
  @Command(aliases = "ban", permission = "guido.ban")
  public void ban(
      CommandSender sender,
      BungeeLocaleFile locale,
      JsonClient client,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Required(name = "Expires", description = "When does the punishment expire") Time expires,
      @Multiple @Required(name = "Reason", description = "The reason to why the player is banned")
          JoinedStrings reason) {
    this.punish(PunishmentType.BAN, sender, player, reason.build(), expires)
        .send(
            client,
            Requests.ifPresentElse(
                punishment -> {
                  ProxiedPlayer proxy = player.toProxy();
                  sender.sendMessage(
                      locale.getComponent(
                          "ban.success", Maps.singleton("nickname", player.getNickname())));
                  if (proxy == null) return;
                  proxy.disconnect(
                      Chat.getLocale(proxy)
                          .getComponent("server.banned", Maps.singleton("reason", reason.build())));
                },
                () -> {
                  // TODO could not be banned
                }));
  }

  @Settings("async")
  @Command(
      aliases = {"warn", "w"},
      permission = "guido.warn")
  public void warn(
      CommandSender sender,
      BungeeLocaleFile locale,
      JsonClient client,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Multiple @Required(name = "Reason", description = "The reason to why the player is banned")
          JoinedStrings reason) {
    this.punish(PunishmentType.WARN, sender, player, reason.build(), new Time(1, Unit.WEEKS))
        .send(
            client,
            Requests.ifPresentElse(
                punishment -> {
                  ProxiedPlayer proxy = player.toProxy();
                  sender.sendMessage(
                      locale.getComponent(
                          "warn.success", Maps.singleton("nickname", player.getNickname())));
                  if (proxy == null) return;
                  proxy.disconnect(
                      Chat.getLocale(proxy)
                          .getComponent("server.warn", Maps.singleton("reason", reason.build())));
                },
                () -> {
                  // TODO could not be banned
                }));
  }

  @Settings("async")
  @Command(aliases = "kick", permission = "guido.kick")
  public void kick(
      CommandSender sender,
      BungeeLocaleFile locale,
      JsonClient client,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Multiple @Required(name = "Reason", description = "The reason to why the player is banned")
          JoinedStrings reason) {
    this.punish(PunishmentType.KICK, sender, player, reason.build(), new Time(1, Unit.MONTHS))
        .send(
            client,
            Requests.ifPresentElse(
                punishment -> {
                  ProxiedPlayer proxy = player.toProxy();
                  sender.sendMessage(
                      locale.getComponent(
                          "kick.success", Maps.singleton("nickname", player.getNickname())));
                  if (proxy == null) return;
                  proxy.disconnect(
                      Chat.getLocale(proxy)
                          .getComponent("server.kicked", Maps.singleton("reason", reason.build())));
                },
                () -> {
                  // TODO could not ban
                }));
  }

  @NonNull
  private RequestBuilder<Punishment> punish(
      @NonNull PunishmentType type,
      @NonNull CommandSender sender,
      @NonNull ProxiedOfflinePlayer punished,
      @Nullable String reason,
      @NonNull Time expires) {
    return Requests.Punishments.create(
        type,
        PunishmentStatus.ACTIVE,
        this.getPunisher(sender),
        punished.getLinkedInfo(),
        new SimpleValuesMap("reason", reason == null ? "No reason" : reason),
        expires.millis() == 0 ? -1 : System.currentTimeMillis() + expires.millis());
  }

  @NonNull
  private LinkableInfo getPunisher(@Nullable CommandSender sender) {
    return sender instanceof ProxiedPlayer
        ? new ProxiedOfflinePlayer((ProxiedPlayer) sender).getLinkedInfo()
        : new SimpleLinkableInfo(LinkableType.NONE, new SimpleValuesMap());
  }
}
