package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.arguments.ArgumentBehaviour;
import com.github.chevyself.starbox.common.Async;
import com.github.chevyself.starbox.common.CommandPermission;
import java.util.HashMap;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.api.utility.Maps;
import me.googas.bungee.data.ProxiedOfflinePlayer;
import me.googas.bungee.lang.BungeeLocaleFile;
import me.googas.bungee.utility.Chat;
import me.googas.net.api.messages.RequestBuilder;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands created for punishments */
public class PunishmentCommands {

  @Async
  @CommandPermission("guido.ban")
  @Command(aliases = "ban")
  public void ban(
      CommandSender sender,
      BungeeLocaleFile locale,
      JsonClient client,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Required(name = "Expires", description = "When does the punishment expire") Time expires,
      @Required(
              name = "Reason",
              description = "The reason to why the player is banned",
              behaviour = ArgumentBehaviour.CONTINUOUS)
          String reason) {
    this.punish(PunishmentType.BAN, sender, player, reason, expires)
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
                          .getComponent("server.banned", Maps.singleton("reason", reason)));
                },
                () -> {
                  // TODO could not be banned
                }));
  }

  @Async
  @CommandPermission("guido.warn")
  @Command(aliases = {"warn", "w"})
  public void warn(
      CommandSender sender,
      BungeeLocaleFile locale,
      JsonClient client,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Required(name = "Reason", description = "The reason to why the player is banned")
          String reason) {
    this.punish(PunishmentType.WARN, sender, player, reason, Time.of(1, Unit.WEEKS))
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
                          .getComponent("server.warn", Maps.singleton("reason", reason)));
                },
                () -> {
                  // TODO could not be banned
                }));
  }

  @Async
  @CommandPermission("guido.kick")
  @Command(aliases = "kick")
  public void kick(
      CommandSender sender,
      BungeeLocaleFile locale,
      JsonClient client,
      @Required(name = "Player", description = "The player to ban") ProxiedOfflinePlayer player,
      @Required(
              name = "Reason",
              description = "The reason to why the player is banned",
              behaviour = ArgumentBehaviour.CONTINUOUS)
          String reason) {
    this.punish(PunishmentType.KICK, sender, player, reason, Time.of(1, Unit.MONTH))
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
                          .getComponent("server.kicked", Maps.singleton("reason", reason)));
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
      String reason,
      @NonNull Time expires) {
    return Requests.Punishments.create(
        type,
        PunishmentStatus.ACTIVE,
        Maps.singleton(
            "global", Maps.objects("reason", reason == null ? "No reason" : reason).build()),
        this.getPunisher(sender),
        punished.getLink(),
        expires.toMillisRound() == 0 ? -1 : System.currentTimeMillis() + expires.toMillisRound());
  }

  @NonNull
  private LinkableInfo getPunisher(CommandSender sender) {
    return sender instanceof ProxiedPlayer
        ? new ProxiedOfflinePlayer((ProxiedPlayer) sender).getLink()
        : new LinkableInfo(LinkableType.NONE, new HashMap<>(), new HashMap<>());
  }
}
