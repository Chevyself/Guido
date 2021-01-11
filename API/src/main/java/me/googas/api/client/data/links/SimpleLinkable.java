package me.googas.api.client.data.links;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.GuidoCatchable;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.team.Team;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.user.UserData;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

public class SimpleLinkable implements Linkable {

  @NonNull @Getter private final LinkableType type;

  @NonNull @Getter private final SimpleValuesMap identification;

  @NonNull @Getter private final SimpleValuesMap recognition;

  @NonNull @Getter private final SimpleValuesMap preferences;

  @NonNull @Getter private final Map<String, Float> stats;

  @NonNull @Getter private final Set<PermissionStack> permissions;

  @Nullable
  @Getter
  @SerializedName(
      value = "linked-id",
      alternate = {"link", "linked"})
  private final String user;

  public SimpleLinkable(
      @NonNull LinkableType type,
      @NonNull SimpleValuesMap identification,
      @NonNull SimpleValuesMap recognition,
      @NonNull SimpleValuesMap preferences,
      @NonNull Map<String, Float> stats,
      @NonNull Set<PermissionStack> permissions,
      @Nullable String user) {
    this.type = type;
    this.identification = identification;
    this.recognition = recognition;
    this.preferences = preferences;
    this.stats = stats;
    this.permissions = permissions;
    this.user = user;
  }

  /** @deprecated gson only */
  public SimpleLinkable() {
    this(
        LinkableType.NONE,
        new SimpleValuesMap(),
        new SimpleValuesMap(),
        new SimpleValuesMap(),
        new HashMap<>(),
        new HashSet<>(),
        null);
  }

  @Override
  public @NonNull GuidoCatchable cache() {
    return this;
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public void sendMessage(@NonNull String message) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void sendLocalized(@NonNull String key) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public @NonNull String getReadable(LocaleFile locale) {
    throw new UnsupportedOperationException("Ues #getSingle");
  }

  @Override
  public void setLinkedUser(UserData user) {
    throw new UnsupportedOperationException("Operation must be done using request");
  }

  @Override
  public @NonNull String getSingle() {
    throw new UnsupportedOperationException("Operation must be done using request");
  }

  @Override
  public String getLinkedUserId() {
    return this.user;
  }

  @Override
  public @NonNull LinkableInfo getInfo() {
    return new SimpleLinkableInfo(this.type, this.identification);
  }

  @Override
  public void setCredits(float value) {
    throw new UnsupportedOperationException("Operation must be done using request");
  }

  @Override
  public @NonNull List<Linkable> getLinks() {
    throw new UnsupportedOperationException("Operation must be done using request");
  }

  @Override
  public float getCredits() {
    throw new UnsupportedOperationException("Operation has not been implemented");
  }

  @Override
  public Team getTeam() {
    throw new UnsupportedOperationException("Operation must be done using request");
  }

  @Nullable
  @Override
  public UserData getLinkedUser() {
    throw new UnsupportedOperationException("Operation must be done using request");
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("type", this.type)
        .append("identification", this.identification)
        .append("recognition", this.recognition)
        .append("preferences", this.preferences)
        .append("stats", this.stats)
        .append("permissions", this.permissions)
        .append("user", this.user)
        .build();
  }
}
