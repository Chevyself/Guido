package me.googas.bot.adapters.schemes.links;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkableInfo;

public class LegacyLinkableInfoScheme implements Scheme<LinkableInfo> {

  @NonNull private final LinkableType type;
  // Identification should not include nickname anymore
  @NonNull private final GuidoValuesMap identification;

  public LegacyLinkableInfoScheme(
      @NonNull LinkableType type, @NonNull GuidoValuesMap identification) {
    this.type = type;
    this.identification = identification;
  }

  /** @deprecated constructor may only be used by gson */
  public LegacyLinkableInfoScheme() {
    this(LinkableType.NONE, new GuidoValuesMap());
  }

  @NonNull
  public GuidoValuesMap getIdentification() {
    if (this.type == LinkableType.MINECRAFT) {
      GuidoValuesMap map = new GuidoValuesMap(this.identification.getMap());
      if (map.get("nickname") != null) map.remove("nickname");
      return map;
    }
    return this.identification;
  }

  @Override
  public @NonNull GuidoLinkableInfo build() {
    return new GuidoLinkableInfo(this.type, this.getIdentification());
  }
}
