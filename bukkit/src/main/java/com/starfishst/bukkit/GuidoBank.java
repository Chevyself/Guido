package com.starfishst.bukkit;

import java.util.Map;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.economy.AbstractBank;
import me.googas.api.economy.AbstractRecord;
import me.googas.api.links.LinkableInfo;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.starbox.Starbox;
import me.googas.starbox.modules.data.DataModule;
import me.googas.starbox.modules.data.economy.Transaction;
import me.googas.starbox.modules.data.economy.TransactionResponse;
import me.googas.starbox.modules.data.economy.TransactionType;
import me.googas.starbox.modules.data.type.Account;
import me.googas.starbox.modules.data.type.Bank;
import me.googas.starbox.modules.placeholders.LocalizedLine;

public class GuidoBank implements Bank, Catchable {

  @NonNull private final AbstractBank abstractBank;

  public GuidoBank(@NonNull AbstractBank abstractBank) {
    this.abstractBank = abstractBank;
  }

  @NonNull
  private Account getAccount(@Nullable LinkableInfo owner) {
    if (owner == null) throw new IllegalArgumentException(this + " does not have an owner");
    return Starbox.getModuleRegistry()
        .require(DataModule.class)
        .getPlayersHandler()
        .getPlayer(owner.getIdUUID("uuid", true));
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull String getId() {
    return this.abstractBank.getId();
  }

  @Override
  public @NonNull Map<String, Double> getAccounts() {
    try {
      AbstractRecord record =
          Requests.Banks.getAccounts(this.getId()).send(Guido.getClient().getConnection());
      if (record != null) {
        return record.getAccounts();
      }
      throw new IllegalArgumentException(
          "There's been an error while trying to get the accounts of " + this.abstractBank);
    } catch (MessengerListenFailException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public @NonNull Transaction withdraw(double amount, @Nullable String context) {
    if (context == null) context = "global";
    double balance = this.getBalance(context);
    try {
      Boolean aBoolean =
          Requests.Banks.withdraw(this.getId(), context, amount)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return new Transaction(TransactionType.WITHDRAW, amount, balance - amount);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
    return new Transaction(
        TransactionType.WITHDRAW,
        amount,
        this.getBalance(context),
        new LocalizedLine("transaction.unknown-error"),
        TransactionResponse.ERROR);
  }

  @Override
  public @NonNull Transaction deposit(double amount, @Nullable String context) {
    if (context == null) context = "global";
    double balance = this.getBalance(context);
    try {
      Boolean aBoolean =
          Requests.Banks.deposit(this.getId(), context, amount)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return new Transaction(TransactionType.DEPOSIT, amount, balance + amount);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
    return new Transaction(
        TransactionType.DEPOSIT,
        amount,
        this.getBalance(context),
        new LocalizedLine("transaction.unknown-error"),
        TransactionResponse.ERROR);
  }

  @Override
  public @NonNull Account getOwner() {
    return this.getAccount(this.abstractBank.getOwner());
  }
}
