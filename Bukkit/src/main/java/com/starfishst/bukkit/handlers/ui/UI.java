package com.starfishst.bukkit.handlers.ui;

import lombok.NonNull;
import me.googas.annotations.Nullable;
import org.bukkit.inventory.InventoryHolder;

public interface UI extends InventoryHolder {

  @Nullable
  Button getButton(int position);

  void setButton(int position, @NonNull Button button);
}
