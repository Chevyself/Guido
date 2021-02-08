package com.starfishst.bukkit;

import com.starfishst.bukkit.api.Guido;
import java.util.Map;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.economy.AbstractBank;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.starbox.modules.data.economy.Transaction;
import me.googas.starbox.modules.data.economy.TransactionResponse;
import me.googas.starbox.modules.data.economy.TransactionType;
import me.googas.starbox.modules.data.type.Account;
import me.googas.starbox.modules.data.type.Bank;
import me.googas.starbox.modules.placeholders.LocalizedLine;

public class GuidoBank implements Bank {

  @NonNull private final AbstractBank abstractBank;

  public GuidoBank(@NonNull AbstractBank abstractBank) {
    this.abstractBank = abstractBank;
  }

  @Override
  public @NonNull Map<String, Double> getAccounts() {
    return this.abstractBank.getAccounts();
  }

  @Override
  public @NonNull String getId() {
    return this.abstractBank.getId();
  }

  @Override
  public @NonNull Transaction withdraw(double amount, @Nullable String context) {
    try {
      Boolean aBoolean =
          Requests.Banks.withdraw(this.getId(), context == null ? "global" : context, amount)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return Bank.super.withdraw(amount, context);
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
    try {
      Boolean aBoolean =
          Requests.Banks.deposit(this.getId(), context == null ? "global" : context, amount)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return Bank.super.deposit(amount, context);
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
    throw new UnsupportedOperationException("Ownership has not been implemented yet");
  }
}
