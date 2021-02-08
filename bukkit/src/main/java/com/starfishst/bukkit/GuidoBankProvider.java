package com.starfishst.bukkit;

import com.starfishst.bukkit.api.Guido;
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

public class GuidoBankProvider implements EconomyHandler.BankProvider {

  @NonNull private final MemoryCache bankCache = new MemoryCache();

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
    AbstractBank abstractBank =
        this.bankCache.getOrSupply(
            AbstractBank.class,
            bank -> bank.getId().equals(account),
            () -> {
              try {
                AbstractBank bank =
                    Requests.Banks.getBank(account).send(Guido.getClient().getConnection());
                if (bank != null) this.bankCache.add(bank);
                return bank;
              } catch (MessengerListenFailException e) {
                e.printStackTrace();
              }
              return null;
            });
    return abstractBank == null ? null : new GuidoBank(abstractBank);
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
