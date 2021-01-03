package com.starfishst.bukkit.listeners.ui.buttons;

import com.starfishst.bukkit.listeners.ui.types.PaginatedInventory;
import com.starfishst.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;

public class BackButton extends SimpleButton {

  public BackButton() {
    super(
        event -> {
          event.setCancelled(true);
          final InventoryHolder holder = event.getInventory().getHolder();
          if (holder instanceof PaginatedInventory) {
            ((PaginatedInventory) holder).previous(event.getWhoClicked());
          }
        },
        ItemFactory.newItem(Material.ARROW, "Back", 1));
  }
}
