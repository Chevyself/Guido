package com.starfishst.bukkit.handlers.ui.buttons;

import com.starfishst.bukkit.handlers.ui.types.PaginatedInventory;
import com.starfishst.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;

public class NextButton extends SimpleButton {

  public NextButton() {
    super(
        event -> {
          event.setCancelled(true);
          final InventoryHolder holder = event.getInventory().getHolder();
          if (holder instanceof PaginatedInventory) {
            ((PaginatedInventory) holder).next(event.getWhoClicked());
          }
        },
        ItemFactory.newItem(Material.ARROW, "Next", 1));
  }
}
