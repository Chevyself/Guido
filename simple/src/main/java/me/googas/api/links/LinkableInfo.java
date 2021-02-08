package me.googas.api.links;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Identifiable;
import me.googas.api.matches.queue.Queueable;

/**
 * LinkedInf represents the linked data as an object to getId it. This means that this contains the
 * way to identify it and the type
 */
public class LinkableInfo implements Queueable, Identifiable {

  @NonNull @Getter private final LinkableType type;
  @NonNull @Getter private final Map<String, Object> identification;
  @NonNull @Getter private final Map<String, Map<String, Double>> stats;

  public LinkableInfo(
      @NonNull LinkableType type,
      @NonNull Map<String, Object> identification,
      @NonNull Map<String, Map<String, Double>> stats) {
    this.type = type;
    this.identification = identification;
    this.stats = stats;
  }

  /** @deprecated this constructor may only be used by gson */
  public LinkableInfo() {
    this(LinkableType.NONE, new HashMap<>(), new HashMap<>());
  }

  /**
   * This method is used to compare this linkable data with a type and provided information
   *
   * @param type the type to compare
   * @param identification the identification to compare
   * @return true if it is the same type and the identification matches
   */
  public boolean compare(@NonNull LinkableType type, @NonNull Map<String, Object> identification) {
    if (this.getType() != type) return false;
    return this.isSimilar(identification);
  }

  /**
   * @see #compare(LinkableType, Map<String, Object)
   * @param info the information of the data comparing
   * @return true if it is the same type and the identification matches
   */
  public boolean compare(@NonNull LinkableInfo info) {
    if (this == info) {
      return true;
    } else {
      return this.compare(info.getType(), info.getIdentification());
    }
  }

  /**
   * @see #compare(LinkableType, Map<String, Object)
   * @param data the other data comparing
   * @return true if it is the same type and the identification matches
   */
  public boolean compare(@NonNull Linkable data) {
    return this.compare(data.getInfo());
  }

  /**
   * Get the data with the given values
   *
   * @return the data
   */
  @Nullable
  public Linkable getLink() {
    return null;
  }

  @Override
  public @NonNull String getSingle() {
    Linkable link = this.getLink();
    return link == null ? "null" : link.getSingle();
  }
}
