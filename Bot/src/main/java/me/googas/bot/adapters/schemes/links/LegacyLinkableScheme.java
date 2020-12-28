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

public class LegacyLinkableScheme implements Scheme<Linkable> {

  @NonNull private final LinkableType type;
  // The only thing that changed is that in the newer versions
  // the identification does not include types that can change
  // this means that the nickname will be changed from identification to recognition
  @NonNull private final GuidoValuesMap identification;
  @NonNull private final GuidoValuesMap preferences;
  @NonNull private final Map<String, Float> stats;
  @NonNull private final Set<PermissionStack> permissions;

  @SerializedName(
      value = "linked-id",
      alternate = {"link", "linked"})
  private final String user;

  public LegacyLinkableScheme(
      @NonNull LinkableType type,
      @NonNull GuidoValuesMap identification,
      @NonNull GuidoValuesMap preferences,
      @NonNull Map<String, Float> stats,
      @NonNull Set<PermissionStack> permissions,
      String user) {
    this.type = type;
    this.identification = identification;
    this.preferences = preferences;
    this.stats = stats;
    this.permissions = permissions;
    this.user = user;
  }

  /** @deprecated this constructor may only be used by gson */
  public LegacyLinkableScheme() {
    this(
        LinkableType.NONE,
        new GuidoValuesMap(),
        new GuidoValuesMap(),
        new HashMap<>(),
        new HashSet<>(),
        null);
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

  @NonNull
  public GuidoValuesMap getRecognition() {
    if (this.type == LinkableType.MINECRAFT) {
      GuidoValuesMap map = new GuidoValuesMap();
      String nickname = this.identification.get("nickname", String.class);
      if (nickname != null) map.put("nickname", nickname);
      return map;
    }
    return this.identification;
  }

  @Override
  public @NonNull GuidoLinkable build() {
    return new GuidoLinkable(
        this.type,
        this.getRecognition(),
        this.user,
        this.getIdentification(),
        this.preferences,
        this.stats,
        this.permissions);
  }
}
