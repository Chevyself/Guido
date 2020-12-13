package me.googas.bot.core.types;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.TeamData;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.api.events.data.links.LinkedDataUnloadedEvent;
import me.googas.bot.api.types.BotLinkable;
import me.googas.bot.api.types.BotPermissible;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.Request;
import me.googas.messaging.json.server.JsonClientThread;
import net.dv8tion.jda.api.entities.User;

/** The implementation of bot linked ata */
public class GuidoLinkable implements BotLinkable, BotPermissible {

  /** The type of the linked data */
  @NonNull private final LinkableType type;
  /** The way to identify this data */
  @NonNull private final GuidoValuesMap identification;
  /** The preferences of the user */
  @NonNull private final GuidoValuesMap preferences;
  /** The stats of the data */
  @NonNull private final Map<String, Float> stats;
  /** The permissions */
  @NonNull private final Set<PermissionStack> permissions;
  /** The id of the linked user */
  @SerializedName("linked-id")
  private String user;

  /**
   * Create the linked data
   *
   * @param type the type of linked data that this is
   * @param user the id of the user linked to this data
   * @param identification the way to identify this data
   * @param preferences the preferences which this data has
   * @param stats the stats of this data
   * @param permissions the permissions of this data
   */
  public GuidoLinkable(
      @NonNull LinkableType type,
      String user,
      @NonNull GuidoValuesMap identification,
      @NonNull GuidoValuesMap preferences,
      @NonNull Map<String, Float> stats,
      @NonNull Set<PermissionStack> permissions) {
    this.type = type;
    this.user = user;
    this.identification = identification;
    this.preferences = preferences;
    this.stats = stats;
    this.permissions = permissions;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLinkable() {
    this(
        LinkableType.NONE,
        null,
        new GuidoValuesMap(),
        new GuidoValuesMap(),
        new HashMap<>(),
        new HashSet<>());
  }

  @Override
  public @NonNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void onRemove() {
    new LinkedDataUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull Map<String, Float> getStats() {
    return this.stats;
  }

  @Override
  public @NonNull LinkableType getType() {
    return this.type;
  }

  @Override
  public @NonNull ValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public float getCredits() {
    return 0;
  }

  @Override
  public void setCredits(float value) {}

  @Override
  public @NonNull ValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public String getLinkedUserId() {
    return this.user;
  }

  @Override
  public void setLinkedUser(UserData user) {
    this.user = user == null ? null : user.getId();
  }

  @Override
  public void sendMessage(@NonNull String message) {
    switch (this.getType()) {
      default:
      case DISCORD_GUILD:
      case DISCORD:
        User user = this.getDiscordUser();
        if (user != null) {
          user.openPrivateChannel()
              .queue(
                  channel -> {
                    if (channel != null) {
                      channel.sendMessage(message).queue();
                    }
                  },
                  exception -> {});
        }
        break;
      case MINECRAFT:
        JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
        UUID uniqueId = this.getUniqueId();
        if (bungee != null && uniqueId != null) {
          bungee.sendRequest(
              new Request<>(
                  Boolean.class,
                  "send-message",
                  Maps.objects("uuid", uniqueId).append("message", message).build()),
              bol -> {});
        }
        break;
    }
  }

  // TODO make that the user is localized and not each different link
  @Override
  public void sendLocalized(@NonNull String key) {
    this.sendMessage(Guido.getLanguageHandler().getDefault().get(key));
  }

  @Override
  public void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders) {
    this.sendMessage(Guido.getLanguageHandler().getDefault().get(key, placeholders));
  }

  @Override
  public @NonNull GuidoLinkableInfo getInfo() {
    return new GuidoLinkableInfo(this.getType(), this.identification);
  }

  @Override
  public boolean isLinked() {
    return this.getLinkedUser() != null;
  }

  @Override
  public TeamData getTeam() {
    return Guido.getDataLoader().getTeam(this);
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
    if (!(o instanceof GuidoLinkable)) return false;

    GuidoLinkable that = (GuidoLinkable) o;

    if (this.type != that.type) return false;
    return this.identification.equals(that.identification);
  }

  @Override
  public int hashCode() {
    int result = this.type.hashCode();
    result = 31 * result + this.identification.hashCode();
    return result;
  }

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @Override
  public @NonNull GuidoLinkable cache() {
    return (GuidoLinkable) BotLinkable.super.cache();
  }
}
