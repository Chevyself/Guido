package me.googas.bot.adapters.schemes.links;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkableInfo;

public class LatestLinkableInfoScheme implements Scheme<LinkableInfo> {

  @NonNull private final LinkableType type;
  @NonNull private final GuidoValuesMap identification;

  public LatestLinkableInfoScheme(
      @NonNull LinkableType type, @NonNull GuidoValuesMap identification) {
    this.type = type;
    this.identification = identification;
  }

  /** @deprecated constructor may only be used by gson */
  public LatestLinkableInfoScheme() {
    this(LinkableType.NONE, new GuidoValuesMap());
  }

  @Override
  public @NonNull GuidoLinkableInfo build() {
    return new GuidoLinkableInfo(this.type, this.identification);
  }
}
