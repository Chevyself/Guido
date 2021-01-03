package com.starfishst.bukkit.util;

import com.starfishst.bukkit.utils.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemFactory {

  @NotNull
  public static ItemStack newItem(@Nullable String materialName, @NotNull String name, int amount) {
    if (materialName != null) {
      final Material material = Material.matchMaterial(materialName);
      if (material != null) {
        return ItemFactory.newItem(material, name, amount);
      } else {
        throw new IllegalArgumentException("Could not get a material from " + materialName);
      }
    } else {
      throw new IllegalArgumentException("Material name cannot be null");
    }
  }

  @NotNull
  public static ItemStack newItem(@NotNull Material material, @NotNull String name, int amount) {
    ItemStack itemStack = new ItemStack(material, amount);
    return ItemFactory.setName(itemStack, name);
  }

  private static ItemStack setName(@NotNull ItemStack itemStack, @NotNull String name) {
    final ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta != null) {
      itemMeta.setDisplayName(BukkitUtils.build(name));
      itemStack.setItemMeta(itemMeta);
      return itemStack;
    } else {
      throw new IllegalArgumentException(itemStack + " is not qualified for name");
    }
  }
}
