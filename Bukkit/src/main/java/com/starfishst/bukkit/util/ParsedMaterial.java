package com.starfishst.bukkit.util;

import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.List;
import lombok.Getter;
import me.googas.commons.builder.ToStringBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParsedMaterial {

  @NotNull @Getter private final Material material;
  @Getter private final short damage;

  private ParsedMaterial(@NotNull Material material, short damage) {
    this.material = material;
    this.damage = damage;
  }

  public ParsedMaterial(@NotNull ItemStack item) {
    this(item.getType(), item.getData().getData());
  }

  public static ParsedMaterial fromString(@NotNull String string) {
    String materialName;
    short damage;
    if (string.contains(":")) {
      final String[] split = string.split(":");
      materialName = split[0];
      damage = Short.parseShort(split[1]);
    } else {
      materialName = string;
      damage = -1;
    }
    Material material = Material.matchMaterial(materialName);
    if (material == null) {
      throw new IllegalArgumentException(string + " is not a valid material");
    } else {
      return new ParsedMaterial(material, damage);
    }
  }

  @NotNull
  public ItemStack getItem(int amount) {
    return this.getItem(amount, null, null);
  }

  public ItemStack getItem(int amount, @Nullable String title, @Nullable List<String> lore) {
    ItemStack itemStack;
    if (this.damage == -1) {
      itemStack = new ItemStack(this.material, amount);
    } else {
      itemStack = new ItemStack(this.material, amount, this.damage);
    }
    final ItemMeta itemMeta = itemStack.getItemMeta();
    if (title != null) {
      itemMeta.setDisplayName(BukkitUtils.build(title));
    }
    if (lore != null) {
      itemMeta.setLore(lore);
    }
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public String toConfig() {
    return this.material.name() + (this.damage > 0 ? ":" + this.damage : "");
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("material", this.material)
        .append("damage", this.damage)
        .build();
  }
}
