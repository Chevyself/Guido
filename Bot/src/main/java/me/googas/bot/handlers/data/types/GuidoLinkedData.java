package me.googas.bot.handlers.data.types;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.api.PermissionStack;
import me.googas.api.UserData;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkedDataType;
import me.googas.bot.api.data.BotLinkedData;
import me.googas.bot.api.data.BotPermissible;
import me.googas.bot.api.events.data.links.LinkedDataLoadedEvent;
import me.googas.bot.api.events.data.links.LinkedDataUnloadedEvent;
import me.googas.bot.handlers.data.types.maps.GuidoValuesMap;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The implementation of bot linked ata */
public class GuidoLinkedData extends Catchable implements BotLinkedData, BotPermissible {

  /** The type of the linked data */
  @NotNull private final LinkedDataType type;
  /** The way to identify this data */
  @NotNull private final GuidoValuesMap identification;
  /** The preferences of the user */
  @NotNull private final GuidoValuesMap preferences;
  /** The stats of the data */
  @NotNull private final Map<String, Float> stats;
  /** The permissions */
  @NotNull private final Set<PermissionStack> permissions;
  /** The id of the linked user */
  @SerializedName("linked-id")
  @Nullable
  private String user;

  /**
   * Create the linked data
   *
   * @param addToCache whether to add it to cache
   * @param type the type of linked data that this is
   * @param user the id of the user linked to this data
   * @param identification the way to identify this data
   * @param preferences the preferences which this data has
   * @param stats the stats of this data
   * @param permissions the permissions of this data
   */
  public GuidoLinkedData(
      boolean addToCache,
      @NotNull LinkedDataType type,
      @Nullable String user,
      @NotNull GuidoValuesMap identification,
      @NotNull GuidoValuesMap preferences,
      @NotNull HashMap<String, Float> stats,
      @NotNull Set<PermissionStack> permissions) {
    super(Time.fromString("3m"), addToCache);
    this.type = type;
    this.user = user;
    this.identification = identification;
    this.preferences = preferences;
    this.stats = stats;
    this.permissions = permissions;
    if (addToCache) {
      new LinkedDataLoadedEvent(this).call();
    }
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLinkedData() {
    this(
        false,
        LinkedDataType.NONE,
        null,
        new GuidoValuesMap(),
        new GuidoValuesMap(),
        new HashMap<>(),
        new HashSet<>());
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new LinkedDataUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Map<String, Float> getStats() {
    return this.stats;
  }

  @Override
  public @NotNull LinkedDataType getType() {
    return this.type;
  }

  @Override
  public @NotNull ValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public @NotNull ValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @Nullable String getLinkedUserId() {
    return this.user;
  }

  @Override
  public void setLinkedUser(@Nullable UserData user) {
    this.user = user == null ? null : user.getId();
  }

  @Override
  public void sendMessage(@NotNull String message) {
    User user = this.getDiscordUser();
    if (user != null) {
      user.openPrivateChannel()
          .queue(
              channel -> {
                if (channel != null) {
                  channel.sendMessage(message).queue();
                }
              });
    }
  }

  @Override
  public @NotNull GuidoLinkedData refresh() {
    return (GuidoLinkedData) super.refresh();
  }

  @Override
  public @NotNull GuidoLinkedInfo getInfo() {
    return new GuidoLinkedInfo(this.getType(), this.identification);
  }

  @Override
  public boolean isLinked() {
    return this.getLinkedUser() != null;
  }

  @Override
  public String toString() {
    return "GuidoLinkedData{"
        + "type="
        + this.type
        + ", identification="
        + this.identification
        + ", preferences="
        + this.preferences
        + ", stats="
        + this.stats
        + ", permissions="
        + this.permissions
        + ", user='"
        + this.user
        + '\''
        + "} "
        + super.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoLinkedData)) return false;

    GuidoLinkedData that = (GuidoLinkedData) o;

    if (this.type != that.type) return false;
    return this.identification.equals(that.identification);
  }

  @Override
  public int hashCode() {
    int result = this.type.hashCode();
    result = 31 * result + this.identification.hashCode();
    return result;
  }
}
