package com.starfishst.bukkit.handlers.ui.buttons;

import com.starfishst.bukkit.handlers.ui.Button;
import com.starfishst.bukkit.handlers.ui.ButtonListener;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SimpleButton implements Button {

  @NotNull @Getter private final ButtonListener listener;
  @NotNull @Getter private final ItemStack item;

  SimpleButton(@NotNull ButtonListener listener, @NotNull ItemStack item) {
    this.listener = listener;
    this.item = item;
  }
}
