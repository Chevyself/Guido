package me.googas.api.economy;

import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.GuidoCatchable;
import me.googas.api.links.LinkableInfo;

public class AbstractBank implements Record, GuidoCatchable {

  @NonNull @Getter private final String id;
  @NonNull @Getter private final Map<String, Double> accounts;
  @Nullable @Getter private final LinkableInfo owner;

  public AbstractBank(
      @NonNull String id, @NonNull Map<String, Double> accounts, @Nullable LinkableInfo owner) {
    this.id = id;
    this.accounts = accounts;
    this.owner = owner;
  }

  @Override
  public @NonNull AbstractBank cache() {
    return (AbstractBank) GuidoCatchable.super.cache();
  }
}
