package com.starfishst.bukkit.commands.economy;

import com.starfishst.commands.bukkit.annotations.Command;
import com.starfishst.commands.bukkit.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Settings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.maps.Maps;
import me.googas.starbox.Starbox;
import me.googas.starbox.modules.data.type.Profile;
import me.googas.starbox.modules.language.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements EconomyCommand {

  @Getter @Setter private boolean enabled;

  @Settings("async")
  @Command(
      aliases = {"balance", "bal"},
      description = "balance.desc")
  public Result balance(
      Language language,
      CommandSender sender,
      @Optional(name = "balance.player.name", description = "balance.player.desc") Profile player) {
    String context = Starbox.getContext();
    if (sender instanceof Player) context = Starbox.buildContext((Player) sender);
    if (player != null) {
      // TODO fix that it has to check if the account has been created for this and pay
      return new Result(
          language.get(
              "balance.other",
              Maps.builder("name", player.getName())
                  .append("balance", this.getCurrency().format(player.getBalance(context)))));
    }
    if (sender instanceof Player) {
      Profile profile = this.getProfile((Player) sender);
      return new Result(
          language.get(
              "balance.own",
              Maps.singleton("balance", this.getCurrency().format(profile.getBalance(context)))));
    }
    return new Result("&cYou must mention at least one player");
  }

  @Override
  public @NonNull String getName() {
    return "balance";
  }

  // TODO balance top
}
