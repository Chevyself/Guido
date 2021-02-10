package me.googas.api.economy;

import java.util.Map;
import lombok.Getter;
import lombok.NonNull;

public class AbstractRecord implements Record {

  @NonNull @Getter private final Map<String, Double> accounts;

  public AbstractRecord(@NonNull Map<String, Double> accounts) {
    this.accounts = accounts;
  }
}
