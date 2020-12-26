package me.googas.bot.core.links;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.api.matches.team.Team;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.user.UserData;
import me.googas.bot.Guido;
import me.googas.bot.api.events.data.links.LinkedDataUnloadedEvent;
import me.googas.bot.api.types.links.BotLinkable;
import me.googas.bot.api.types.permissions.BotPermissible;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.Request;
import me.googas.messaging.json.server.JsonClientThread;
import net.dv8tion.jda.api.entities.User;

/** The implementation of bot linked ata */
public class GuidoLinkable implements BotLinkable, BotPermissible {

  @NonNull private final LinkableType type;
  @NonNull private final GuidoValuesMap identification;
  @NonNull private final GuidoValuesMap recognition;
  @NonNull private final GuidoValuesMap preferences;
  @NonNull private final Map<String, Float> stats;
  @NonNull private final Set<PermissionStack> permissions;

  @SerializedName(
      value = "linked-id",
      alternate = {"link", "linked"})
  private String user;

  /**
   * Create the linked data
   *
   * @param type the type of linked data that this is
   * @param recognition the recognition map of the link
   * @param user the id of the user linked to this data
   * @param identification the way to identify this data
   * @param preferences the preferences which this data has
   * @param stats the stats of this data
   * @param permissions the permissions of this data
   */
  public GuidoLinkable(
      @NonNull LinkableType type,
      @NonNull GuidoValuesMap recognition,
      String user,
      @NonNull GuidoValuesMap identification,
      @NonNull GuidoValuesMap preferences,
      @NonNull Map<String, Float> stats,
      @NonNull Set<PermissionStack> permissions) {
    this.type = type;
    this.recognition = recognition;
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
        new GuidoValuesMap(),
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
  public @NonNull ValuesMap getRecognition() {
    return this.recognition;
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
        UUID uniqueId = new MinecraftLinkable(this).getUuid();
        if (bungee != null) {
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
  public Team getTeam() {
    return Guido.getDataLoader().getTeam(this);
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

  @Override
  public @NonNull GuidoLinkable cache() {
    return (GuidoLinkable) BotLinkable.super.cache();
  }
}
