package com.starfishst.bukkit.listeners.ui.types;

import com.starfishst.bukkit.listeners.ui.Button;
import com.starfishst.bukkit.listeners.ui.UI;
import com.starfishst.bukkit.listeners.ui.buttons.EmptyButton;
import com.starfishst.bukkit.util.Numbers;
import com.starfishst.bukkit.util.ParsedMaterial;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomInventory implements UI {

  @NotNull private final HashMap<Integer, Button> buttons = new HashMap<>();
  @NotNull private final Inventory inventory;

  public CustomInventory(int size, @Nullable String title) {
    this.inventory =
        Bukkit.createInventory(this, size, title == null ? "Inventory" : BukkitUtils.build(title));
  }

  @Override
  public @Nullable Button getButton(int position) {
    return this.buttons.get(position);
  }

  @Override
  public void setButton(int position, @NotNull Button button) {
    if (position > this.inventory.getSize()) {
      throw new IllegalArgumentException("Position cannot be greater than the inventory size");
    } else {
      this.buttons.put(position, button);
      this.inventory.setItem(position, button.getItem());
    }
  }

  @Override
  public @NotNull Inventory getInventory() {
    // TODO probably change this to make it configurable
    Numbers.getBorders(this.inventory)
        .forEach(
            position -> {
              if (this.buttons.get(position) == null) {
                this.setButton(
                    position, new EmptyButton(ParsedMaterial.fromString("160:7").getItem(1)));
              }
            });
    Numbers.getSpaces(this.inventory)
        .forEach(
            position -> {
              if (this.buttons.get(position) == null) {
                this.setButton(
                    position, new EmptyButton(ParsedMaterial.fromString("160:8").getItem(1)));
              }
            });
    this.buttons.forEach((position, button) -> this.inventory.setItem(position, button.getItem()));
    return this.inventory;
  }
}
