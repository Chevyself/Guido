package me.googas.api.economy;

import java.util.Map;
import lombok.NonNull;

public interface Record {

  default boolean has(@NonNull String context, double amount) {
    return amount >= this.getBalance(context);
  }

  default boolean withdraw(@NonNull String context, double amount) {
    if (this.has(context, amount)) {
      double result = this.getBalance(context) - amount;
      result = result < 0 ? 0 : result;
      this.getAccounts().put(context, result);
      return true;
    }
    return false;
  }

  default boolean deposit(@NonNull String context, double amount) {
    double result = this.getBalance(context) + amount;
    this.getAccounts().put(context, result);
    return true;
  }

  default double getBalance(@NonNull String context) {
    return this.getAccounts().getOrDefault(context, 0D);
  }

  @NonNull
  Map<String, Double> getAccounts();
}
