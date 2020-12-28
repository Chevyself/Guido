package me.googas.bot.adapters.links;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.links.Linkable;
import me.googas.bot.adapters.SchemeAdapter;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.bot.adapters.schemes.links.LatestLinkableScheme;
import me.googas.bot.adapters.schemes.links.LegacyLinkableScheme;

public class LinkableAdapter implements SchemeAdapter<Linkable> {

  @NonNull private final Map<String, Class<? extends Scheme<Linkable>>> schemes = new HashMap<>();

  @Getter @Setter private boolean emptyAsLatest;

  public LinkableAdapter(boolean emptyAsLatest) {
    this.schemes.put("legacy", LegacyLinkableScheme.class);
    this.schemes.put("PRE-3", LatestLinkableScheme.class);
    this.emptyAsLatest = emptyAsLatest;
  }

  @Override
  public @NonNull Map<String, Class<? extends Scheme<Linkable>>> getSchemes() {
    return this.schemes;
  }

  @Override
  public @NonNull String getLatest() {
    return "PRE-3";
  }
}
