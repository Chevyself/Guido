package me.googas.api.economy;

import java.util.Map;
import lombok.Getter;
import lombok.NonNull;

public class Bank implements Record {

  @NonNull @Getter private final String id;
  @NonNull @Getter private final Map<String, Map<String, Double>> accounts;

  public Bank(@NonNull String id, @NonNull Map<String, Map<String, Double>> accounts) {
    this.id = id;
    this.accounts = accounts;
  }
}
