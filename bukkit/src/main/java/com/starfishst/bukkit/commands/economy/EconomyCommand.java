package com.starfishst.bukkit.commands.economy;

import com.starfishst.bukkit.commands.GuidoCommand;
import lombok.NonNull;
import me.googas.starbox.Starbox;
import me.googas.starbox.modules.data.DataModule;
import me.googas.starbox.modules.data.economy.EconomyHandler;
import me.googas.starbox.modules.data.economy.Transaction;
import me.googas.starbox.modules.data.economy.TransactionType;
import me.googas.starbox.modules.data.type.Currency;
import me.googas.starbox.modules.data.type.Profile;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface EconomyCommand extends GuidoCommand {

  @NonNull
  default EconomyHandler economy() {
    return this.data().getEconomyHandler();
  }

  @NonNull
  default DataModule data() {
    return Starbox.getModuleRegistry().require(DataModule.class);
  }

  @NonNull
  default Profile getProfile(@NonNull OfflinePlayer player) {
    return this.data().getPlayersHandler().getPlayer(player);
  }

  default boolean has(@NonNull CommandSender sender, double amount, @NonNull String context) {
    if (!(sender instanceof Player)) return true;
    return this.getProfile((Player) sender).hasBalance(amount, context);
  }

  @NonNull
  default Transaction withdraw(
      @NonNull CommandSender sender, double amount, @NonNull String context) {
    if (!(sender instanceof Player)) return new Transaction(TransactionType.WITHDRAW, 0, 0);
    Profile profile = this.getProfile((Player) sender);
    return profile.withdraw(amount, context);
  }

  @NonNull
  default Transaction deposit(
      @NonNull CommandSender sender, double amount, @NonNull String context) {
    if (!(sender instanceof Player)) return new Transaction(TransactionType.DEPOSIT, 0, 0);
    Profile profile = this.getProfile((Player) sender);
    return profile.deposit(amount, context);
  }

  @NonNull
  default Currency getCurrency() {
    return this.economy().getCurrency();
  }
}
