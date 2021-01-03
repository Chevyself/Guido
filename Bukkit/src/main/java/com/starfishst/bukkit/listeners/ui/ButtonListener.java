package com.starfishst.bukkit.listeners.ui;

import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ButtonListener {

  void onClick(@NonNull InventoryClickEvent event);
}
