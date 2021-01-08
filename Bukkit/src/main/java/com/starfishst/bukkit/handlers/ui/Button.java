package com.starfishst.bukkit.handlers.ui;

import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public interface Button {

  @NonNull
  ButtonListener getListener();

  @NonNull
  ItemStack getItem();
}
