package com.starfishst.bukkit.handlers.ui.buttons;

import com.starfishst.bukkit.handlers.ui.Button;
import com.starfishst.bukkit.handlers.ui.ButtonListener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EmptyButton implements Button {

  @NotNull private final ItemStack item;

  public EmptyButton(@NotNull ItemStack item) {
    this.item = item;
  }

  @Override
  public @NotNull ButtonListener getListener() {
    return event -> event.setCancelled(true);
  }

  @Override
  public @NotNull ItemStack getItem() {
    return this.item;
  }
}
