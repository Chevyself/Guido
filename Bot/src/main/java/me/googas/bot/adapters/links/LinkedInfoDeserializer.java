package me.googas.bot.adapters.links;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.links.LinkableInfo;
import me.googas.bot.adapters.SchemeDeserializer;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.bot.adapters.schemes.links.LatestLinkableInfoScheme;
import me.googas.bot.adapters.schemes.links.LegacyLinkableInfoScheme;

public class LinkedInfoDeserializer implements SchemeDeserializer<LinkableInfo> {

  @NonNull
  private final Map<String, Class<? extends Scheme<LinkableInfo>>> schemes = new HashMap<>();

  @Getter @Setter private boolean emptyAsLatest;

  public LinkedInfoDeserializer(boolean emptyAsLatest) {
    this.schemes.put("legacy", LegacyLinkableInfoScheme.class);
    this.schemes.put("PRE-3", LatestLinkableInfoScheme.class);
    this.emptyAsLatest = emptyAsLatest;
  }

  @Override
  public @NonNull Map<String, Class<? extends Scheme<LinkableInfo>>> getSchemes() {
    return this.schemes;
  }

  @Override
  public @NonNull String getLatest() {
    return "PRE-3";
  }
}
