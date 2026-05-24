package me.googas.api.loader;

import lombok.NonNull;
import me.googas.api.economy.AbstractBank;

public interface BanksLoader extends DataLoader {

  AbstractBank getBank(@NonNull String id);

  boolean delete(@NonNull String id);
}
