package com.starfishst.bukkit.handlers.placeholders;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.dependencies.papi.PAPIPlaceholderHandler;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.annotations.Nullable;
import org.bukkit.OfflinePlayer;

public class PlaceholderHandler implements Handler {

  public static final Pattern PATTERN = Pattern.compile("%.*?%");

  @NonNull @Delegate private final Set<Placeholder> placeholders = new HashSet<>();

  @Nullable
  public Placeholder getPlaceholder(@NonNull String name) {
    for (Placeholder placeholder : this.placeholders) {
      if (placeholder.getName().equalsIgnoreCase(name)) return placeholder;
    }
    return null;
  }

  public String build(@NonNull OfflinePlayer player, @NonNull String raw) {
    if (Guido.isPAPIConnected()) {
      return Guido.getHandlerRegistry()
          .requireHandler(PAPIPlaceholderHandler.class)
          .build(player, raw);
    } else {
      Matcher matcher = PlaceholderHandler.PATTERN.matcher(raw);
      while (matcher.find()) {
        String name = matcher.group().replace("%", "");
        Placeholder placeholder = this.getPlaceholder(name);
        if (placeholder != null) raw = raw.replace("%" + name + "%", placeholder.build(player));
      }
      return BukkitUtils.build(raw);
    }
  }

  @Override
  public @NonNull String getName() {
    return "placeholders";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
