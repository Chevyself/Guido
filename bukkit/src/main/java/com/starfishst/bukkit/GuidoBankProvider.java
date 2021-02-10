package com.starfishst.bukkit;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.economy.AbstractBank;
import me.googas.commons.RandomUtils;
import me.googas.commons.cache.MemoryCache;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.starbox.modules.data.economy.EconomyHandler;
import me.googas.starbox.modules.data.economy.Transaction;
import me.googas.starbox.modules.data.economy.TransactionResponse;
import me.googas.starbox.modules.data.economy.TransactionType;
import me.googas.starbox.modules.data.type.Bank;
import me.googas.starbox.modules.data.type.Profile;
import me.googas.starbox.modules.placeholders.LocalizedLine;
import org.bukkit.Bukkit;

public class GuidoBankProvider implements EconomyHandler.BankProvider {

  @NonNull private final MemoryCache bankCache = new MemoryCache();

  @NonNull
  public GuidoBankProvider startTask() {
    Bukkit.getScheduler().runTaskTimerAsynchronously(Guido.getPlugin(), this.bankCache, 0, 20);
    return this;
  }

  @Override
  public @NonNull Transaction createBank(@Nullable String account, @NonNull Profile profile) {
    if (!(profile instanceof GuidoProfile))
      return new Transaction(
          TransactionType.CREATE_BANK,
          0,
          0,
          new LocalizedLine("transaction.create-bank.no-profile"),
          TransactionResponse.ERROR);
    try {
      AbstractBank bank =
          Requests.Banks.create(
                  account == null ? RandomUtils.nextString(16) : account,
                  ((GuidoProfile) profile).getInfo())
              .send(Guido.getClient().getConnection());
      if (bank != null) {
        this.bankCache.add(bank);
        return new Transaction(TransactionType.CREATE_BANK, 0, 0);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
    return new Transaction(
        TransactionType.CREATE_BANK,
        0,
        0,
        new LocalizedLine("transaction.create-bank.unknown-error"),
        TransactionResponse.ERROR);
  }

  @Override
  public @NonNull Transaction deleteBank(@NonNull String account) {
    Bank bank = this.getBank(account);
    if (bank == null) {
      return new Transaction(
          TransactionType.DELETE_BANK,
          0,
          0,
          new LocalizedLine("transaction.delete-bank.not-found"),
          TransactionResponse.ERROR);
    }
    try {
      Requests.Banks.delete(account).send(Guido.getClient().getConnection());
      return new Transaction(TransactionType.DELETE_BANK, 0, 0);
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
    return new Transaction(
        TransactionType.DELETE_BANK,
        0,
        0,
        new LocalizedLine("transaction.delete-bank.unknown-error"),
        TransactionResponse.ERROR);
  }

  @Nullable
  @Override
  public Bank getBank(@NonNull String account) {
    return this.bankCache.getOrSupply(
        GuidoBank.class,
        bank -> bank.getId().equals(account),
        () -> {
          try {
            AbstractBank bank =
                Requests.Banks.getBank(account).send(Guido.getClient().getConnection());
            if (bank != null) {
              GuidoBank loaded = new GuidoBank(bank);
              this.bankCache.add(loaded);
              return loaded;
            }
            return null;
          } catch (MessengerListenFailException e) {
            e.printStackTrace();
          }
          return null;
        });
  }

  @Override
  public boolean hasAccount(@NonNull Profile profile) {
    return false;
  }

  @Override
  public @NonNull List<Bank> getBanks() {
    return new ArrayList<>();
  }
}
