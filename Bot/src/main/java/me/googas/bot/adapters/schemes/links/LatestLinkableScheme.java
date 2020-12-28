package me.googas.bot.adapters.schemes.links;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkable;

public class LatestLinkableScheme implements Scheme<Linkable> {

  @NonNull private final LinkableType type;
  @NonNull private final GuidoValuesMap identification;
  @NonNull private final GuidoValuesMap recognition;
  @NonNull private final GuidoValuesMap preferences;
  @NonNull private final Map<String, Float> stats;
  @NonNull private final Set<PermissionStack> permissions;

  @SerializedName(
      value = "linked-id",
      alternate = {"link", "linked"})
  private final String user;

  public LatestLinkableScheme(
      @NonNull LinkableType type,
      @NonNull GuidoValuesMap identification,
      @NonNull GuidoValuesMap recognition,
      @NonNull GuidoValuesMap preferences,
      @NonNull Map<String, Float> stats,
      @NonNull Set<PermissionStack> permissions,
      String user) {
    this.type = type;
    this.identification = identification;
    this.recognition = recognition;
    this.preferences = preferences;
    this.stats = stats;
    this.permissions = permissions;
    this.user = user;
  }

  /** @deprecated this constructor may only be used by gson */
  public LatestLinkableScheme() {
    this(
        LinkableType.NONE,
        new GuidoValuesMap(),
        new GuidoValuesMap(),
        new GuidoValuesMap(),
        new HashMap<>(),
        new HashSet<>(),
        null);
  }

  @Override
  public @NonNull GuidoLinkable build() {
    return new GuidoLinkable(
        this.type,
        this.recognition,
        this.user,
        this.identification,
        this.preferences,
        this.stats,
        this.permissions);
  }
}
