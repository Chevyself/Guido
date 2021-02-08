package com.starfishst.bukkit;

import com.starfishst.bukkit.api.Guido;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.permissions.AbstractPermission;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.starbox.modules.data.economy.Transaction;
import me.googas.starbox.modules.data.economy.TransactionResponse;
import me.googas.starbox.modules.data.economy.TransactionType;
import me.googas.starbox.modules.data.type.Profile;
import me.googas.starbox.modules.placeholders.LocalizedLine;

public class GuidoProfile implements Profile {

  @NonNull private final Linkable linkable;

  public GuidoProfile(@NonNull Linkable linkable) {
    this.linkable = linkable;
  }

  public @NonNull LinkableInfo getInfo() {
    return this.linkable.getInfo();
  }

  @Override
  public @NonNull Map<String, Map<String, Double>> getStats() {
    return this.linkable.getStats();
  }

  @Override
  public @NonNull Map<String, Map<String, Boolean>> getPermissions() {
    return this.linkable.toMap();
  }

  @Nullable
  @Override
  public String getPrefix(@Nullable String context) {
    return this.linkable.getString(context, "prefix", "");
  }

  @Nullable
  @Override
  public String getSuffix(@Nullable String context) {
    return this.linkable.getString(context, "suffix", "");
  }

  @Override
  public void setPrefix(@Nullable String context, @Nullable String prefix) {
    this.linkable.setString(context, "prefix", prefix);
  }

  @Override
  public void setSuffix(@Nullable String context, @Nullable String suffix) {
    this.linkable.setString(context, "prefix", suffix);
  }

  @Override
  public @NonNull Map<String, Map<String, Object>> getInformation() {
    return this.linkable.getInformation();
  }

  @Override
  public @NonNull Map<String, Double> getAccounts() {
    return this.linkable.getAccounts();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull UUID getUniqueId() {
    return this.linkable.getIdUUID("uuid", true);
  }

  @Override
  public void set(@Nullable String context, @NonNull String node, @NonNull Object value) {
    try {
      Boolean aBoolean =
          Requests.Links.set(
                  this.linkable.getInfo(), context == null ? "global" : context, node, value)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        Profile.super.set(context, node, value);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean addPermission(@Nullable String context, @NonNull String permission) {
    try {
      Boolean aBoolean =
          Requests.Links.permission(
                  this.linkable.getInfo(),
                  context == null ? "global" : context,
                  new AbstractPermission(permission, true, -1))
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return Profile.super.addPermission(context, permission);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean removePermission(@Nullable String context, @NonNull String permission) {
    try {
      Boolean aBoolean =
          Requests.Links.removePermission(
                  this.linkable.getInfo(), context == null ? "global" : context, permission)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return Profile.super.removePermission(context, permission);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public @NonNull Transaction withdraw(double amount, @Nullable String context) {
    try {
      Boolean aBoolean =
          Requests.Links.withdraw(
                  this.linkable.getInfo(), context == null ? "global" : context, amount)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return Profile.super.withdraw(amount, context);
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
          Requests.Links.deposit(
                  this.linkable.getInfo(), context == null ? "global" : context, amount)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return Profile.super.deposit(amount, context);
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
}
