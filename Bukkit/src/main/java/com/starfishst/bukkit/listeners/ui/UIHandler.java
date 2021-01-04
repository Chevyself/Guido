package com.starfishst.bukkit.listeners.ui;

import com.starfishst.bukkit.api.events.Handler;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class UIHandler implements Handler {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClick(InventoryClickEvent event) {
    final InventoryHolder holder = event.getInventory().getHolder();
    if (!(holder instanceof UI)) return;
    final Button button = ((UI) holder).getButton(event.getSlot());
    if (button != null) button.getListener().onClick(event);
  }

  @Override
  public @NonNull String getName() {
    return "UI";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
