package me.googas.api.economy;

import java.util.Map;
import lombok.NonNull;

public interface Record {

  @NonNull
  Map<String, Map<String, Double>> getAccounts();
}
