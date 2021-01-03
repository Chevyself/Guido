package com.starfishst.bukkit.listeners.ui.types;

import com.starfishst.bukkit.listeners.ui.Button;
import com.starfishst.bukkit.listeners.ui.UI;
import com.starfishst.bukkit.listeners.ui.buttons.BackButton;
import com.starfishst.bukkit.listeners.ui.buttons.NextButton;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.HashMap;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public class PaginatedInventory implements UI {

  @NonNull private final HashMap<Integer, Button> buttons = new HashMap<>();
  @NonNull private final HashMap<Integer, Button> toolbar = new HashMap<>();
  private final int defaultSize;
  @NonNull private final String title;
  private int page;

  public PaginatedInventory(int defaultSize, @NonNull String title) {
    this.defaultSize = defaultSize;
    this.page = 0;
    this.title = BukkitUtils.build(title);
  }

  public PaginatedInventory(@NonNull String title) {
    this(0, title);
  }

  private int size() {
    if (this.defaultSize > 0) {
      return this.defaultSize;
    } else {
      if (this.lastSlot() >= 53) {
        return 54;
      } else {
        int size = 54;
        while (size != 9) {
          size -= 9;
          if (this.lastSlot() > size) {
            break;
          }
        }
        return size;
      }
    }
  }

  private int realSize() {
    return this.size() - 9;
  }

  private int lastSlot() {
    int last = 0;
    for (Integer position : this.buttons.keySet()) {
      if (last < position) {
        last = position;
      }
    }
    return last;
  }

  private int lastEmptySlot() {
    return this.buttons.isEmpty() ? 0 : this.lastSlot() + 1;
  }

  public void previous(@NonNull HumanEntity entity) {
    if (this.previous()) {
      this.refresh(entity);
    }
  }

  public void next(@NonNull HumanEntity entity) {
    if (this.next()) {
      this.refresh(entity);
    }
  }

  private void refresh(@NonNull HumanEntity entity) {
    entity.closeInventory();
    entity.openInventory(this.getInventory());
  }

  private int finalPage() {
    return ((this.lastEmptySlot() + this.realSize() - 1) / this.realSize()) - 1;
  }

  private boolean previous() {
    if (this.finalPage() > 0) {
      this.page--;
      return true;
    } else {
      return false;
    }
  }

  private boolean next() {
    if (this.page < this.finalPage()) {
      this.page++;
      return true;
    } else {
      return false;
    }
  }

  public void addButton(@NonNull Button button) {
    this.buttons.put(this.lastEmptySlot(), button);
  }

  private void setToolbar(int position, @Nullable Button button) {
    if (position >= 0 && position <= 8) {
      if (button != null) {
        this.toolbar.put(position, button);
      } else {
        this.toolbar.remove(position);
      }
    } else {
      throw new IllegalArgumentException(
          position + " is out of bounds. Position must be between 0 and 8");
    }
  }

  private void addToolbar(@NonNull Inventory inventory) {
    if (this.page > 0) {
      this.setToolbar(0, new BackButton());
    } else {
      this.setToolbar(0, null);
    }
    if (this.page < this.finalPage()) {
      this.setToolbar(8, new NextButton());
    } else {
      this.setToolbar(8, null);
    }
    this.toolbar.forEach(
        (position, button) -> {
          inventory.setItem(position + this.realSize(), button.getItem());
        });
  }

  private void addItems(Inventory inventory) {
    this.buttons.forEach(
        (position, button) -> {
          int realPosition = position - (this.page * this.realSize());
          if (realPosition >= 0 && realPosition < this.realSize()) {
            inventory.setItem(realPosition, button.getItem());
          }
        });
  }

  @Override
  public @NonNull Button getButton(int position) {
    if (position < this.realSize()) {
      return this.buttons.get(position);
    } else {
      return this.toolbar.get(position - 45);
    }
  }

  @Override
  public void setButton(int position, @NonNull Button button) {
    this.buttons.put(position, button);
  }

  @Override
  public Inventory getInventory() {
    Inventory inventory = Bukkit.createInventory(this, this.size(), this.title);
    this.addToolbar(inventory);
    this.addItems(inventory);
    return inventory;
  }
}
