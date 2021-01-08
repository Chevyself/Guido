package com.starfishst.bukkit.handlers.ui;

import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ButtonListener {

  void onClick(@NonNull InventoryClickEvent event);
}
