package com.starfishst.bukkit.commands.economy;

import com.starfishst.commands.bukkit.annotations.Command;
import com.starfishst.commands.bukkit.result.Result;
import com.starfishst.core.annotations.Required;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.maps.Maps;
import me.googas.starbox.Starbox;
import me.googas.starbox.modules.data.economy.Transaction;
import me.googas.starbox.modules.data.economy.TransactionResponse;
import me.googas.starbox.modules.data.type.Profile;
import me.googas.starbox.modules.language.Language;
import me.googas.starbox.modules.language.LanguageModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements EconomyCommand {

  @Getter @Setter private boolean enabled;

  @Command(aliases = "pay", description = "pay.desc")
  public Result pay(
      CommandSender sender,
      Language language,
      LanguageModule module,
      @Required(name = "pay.player.name", description = "pay.player.desc") Profile profile,
      @Required(name = "pay.amount.name", description = "pay.amount.desc") double amount) {
    String context = Starbox.getContext();
    if (sender instanceof Player) context = Starbox.buildContext((Player) sender);
    if (sender.hasPermission("guido.pay.ignore-balance") || this.has(sender, amount, context)) {
      if (!sender.hasPermission("guido.pay.ignore-balance")) {
        Transaction transaction = this.withdraw(sender, amount, context);
        if (transaction.getTransactionResponse() == TransactionResponse.ERROR)
          return new Result(language.get("pay.withdraw-error"));
      }
      Transaction deposit = profile.deposit(amount, context);
      if (!sender.hasPermission("guido.pay.ignore-balance")) {
        if (deposit.getTransactionResponse() == TransactionResponse.ERROR) {
          this.deposit(sender, amount, context);
          return new Result(language.get("pay.deposit-error"));
        }
      }
      Player onlinePlayer = Bukkit.getPlayer(profile.getUniqueId());
      String formatted = this.getCurrency().format(amount);
      if (onlinePlayer != null) {
        onlinePlayer.sendMessage(
            module
                .getLanguage(onlinePlayer)
                .get(
                    "pay.received",
                    Maps.builder("sender", sender.getName()).append("amount", formatted)));
      }
      return new Result(
          language.get(
              "pay.done", Maps.builder("amount", formatted).append("name", profile.getName())));
    } else {
      return new Result(language.get("pay.no-money"));
    }
  }

  @Override
  public @NonNull String getName() {
    return "pay";
  }
}
