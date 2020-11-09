package me.googas.bot.core.types;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.googas.api.links.LinkableDataType;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.api.events.data.links.LinkedDataUnloadedEvent;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.api.types.BotPermissible;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.bot.core.util.console.Console;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.Request;
import me.googas.messaging.json.server.JsonClientThread;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The implementation of bot linked ata */
public class GuidoLinkableData implements BotLinkableData, BotPermissible {

  /** The type of the linked data */
  @NotNull private final LinkableDataType type;
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
   * @param type the type of linked data that this is
   * @param user the id of the user linked to this data
   * @param identification the way to identify this data
   * @param preferences the preferences which this data has
   * @param stats the stats of this data
   * @param permissions the permissions of this data
   */
  public GuidoLinkableData(
      @NotNull LinkableDataType type,
      @Nullable String user,
      @NotNull GuidoValuesMap identification,
      @NotNull GuidoValuesMap preferences,
      @NotNull Map<String, Float> stats,
      @NotNull Set<PermissionStack> permissions) {
    this.type = type;
    this.user = user;
    this.identification = identification;
    this.preferences = preferences;
    this.stats = stats;
    this.permissions = permissions;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLinkableData() {
    this(
        LinkableDataType.NONE,
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
  public void onRemove() {
    new LinkedDataUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NotNull Map<String, Float> getStats() {
    return this.stats;
  }

  @Override
  public @NotNull LinkableDataType getType() {
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
                  exception ->
                      Console.debug(
                          "Cannot send message to user "
                              + user.getAsTag()
                              + " because:  "
                              + exception.getMessage()));
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
              bol -> {
                Console.debug(
                    "Trying to send message "
                        + message
                        + " to "
                        + uniqueId
                        + " was success ? "
                        + bol);
              });
        }
        break;
    }
  }

  // TODO make that the user is localized and not each different link
  @Override
  public void sendLocalized(@NotNull String key) {
    this.sendMessage(Guido.getLanguageHandler().getDefault().get(key));
  }

  @Override
  public void sendLocalized(@NotNull String key, @NotNull Map<String, String> placeholders) {
    this.sendMessage(Guido.getLanguageHandler().getDefault().get(key, placeholders));
  }

  @Override
  public @NotNull GuidoLinkableInfo getInfo() {
    return new GuidoLinkableInfo(this.getType(), this.identification);
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
    if (!(o instanceof GuidoLinkableData)) return false;

    GuidoLinkableData that = (GuidoLinkableData) o;

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
  public @NotNull GuidoLinkableData cache() {
    return (GuidoLinkableData) BotLinkableData.super.cache();
  }
}
