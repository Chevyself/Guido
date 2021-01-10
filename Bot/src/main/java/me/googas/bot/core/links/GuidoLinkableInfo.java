package me.googas.bot.core.links;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoValuesMap;

/** The uncompleted data from a linked data */
public class GuidoLinkableInfo extends SimpleLinkableInfo {

  /** The version of serialization for the scheme */
  @NonNull @Getter private final String version = "PRE-3";

  /**
   * Create the uncompleted data
   *
   * @param type the type of data
   * @param identification the way to identify the data
   */
  public GuidoLinkableInfo(@NonNull LinkableType type, @NonNull ValuesMap identification) {
    super(type, new SimpleValuesMap(identification.getMap()));
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLinkableInfo() {
    this(LinkableType.NONE, new GuidoValuesMap());
  }

  @Override
  public @NonNull Map<String, Float> getStats() {
    Linkable link = this.getLink();
    return link == null ? new HashMap<>() : link.getStats();
  }

  @Override
  public @NonNull String getSingle() {
    Linkable link = this.getLink();
    return link == null ? "invalid" : link.getSingle();
  }

  @Override
  public Linkable getLink() {
    return Guido.getHandlers()
        .getLoader()
        .getLinks()
        .getLink(this.getType(), this.getIdentification());
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof GuidoLinkableInfo)) return false;
    GuidoLinkableInfo that = (GuidoLinkableInfo) object;
    return this.compare(that);
  }
}
