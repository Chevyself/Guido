package me.googas.api.loader;

import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.economy.AbstractBank;

public interface BanksLoader extends DataLoader {
  @Nullable
  AbstractBank getBank(@NonNull String id);

  boolean delete(@NonNull String id);
}
