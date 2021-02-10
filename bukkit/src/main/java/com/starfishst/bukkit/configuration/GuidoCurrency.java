package com.starfishst.bukkit.configuration;

import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.starbox.modules.data.type.Currency;
import org.bukkit.configuration.ConfigurationSection;

public class GuidoCurrency implements Currency {

  @NonNull @Getter private final String name;
  @NonNull @Getter private final String namePlural;
  @NonNull @Getter private final double start;

  public GuidoCurrency(@NonNull String name, @NonNull String namePlural, double start) {
    this.name = name;
    this.namePlural = namePlural;
    this.start = start;
  }

  public GuidoCurrency() {
    this("Dollar", "Dollars", 0);
  }

  @NonNull
  public static GuidoCurrency load(@Nullable ConfigurationSection section) {
    if (section == null) return new GuidoCurrency();
    return new GuidoCurrency(
        section.getString("name", "Dollar"),
        section.getString("plural", "Dollars"),
        section.getDouble("start", 0));
  }

  @Override
  public @NonNull String format(double value) {
    return Currency.super.format(value) + "$";
  }
}
