package me.googas.bot.core.links;

import com.google.gson.annotations.SerializedName;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.ValuesMap;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.team.Team;
import me.googas.api.permissions.Permissible;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.server.receptors.LinkReceptors;
import me.googas.api.user.UserData;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.data.links.LinkableUnloadedEvent;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionAddedEvent;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionRemovedEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.json.server.JsonClientThread;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

/** The implementation of bot linked ata */
public class GuidoLinkable implements Permissible, Linkable, BotCatchable {

  @NonNull
  public static LinkReceptors.LinkableSupplier SUPPLIER =
      (type, recognition, identification, preferences, stats, permissions) ->
          new GuidoLinkable(
                  type,
                  new GuidoValuesMap(recognition),
                  null,
                  new GuidoValuesMap(identification),
                  new GuidoValuesMap(preferences),
                  stats,
                  permissions)
              .cache();
  /** The version of serialization for the scheme */
  @NonNull @Getter private final String version = "PRE-3";

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
    new LinkableUnloadedEvent(this).call();
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
      case DISCORD:
        JDA jda = Guido.getConnection().validatedJda();
        User user = this.requireDiscordRef().getUser(jda);
        if (user != null && user != jda.getSelfUser()) {
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
        if (bungee != null) {
          Requests.Bungee.sendMessage(this.requireMinecraftRef().getUuid(), message).queue(bungee);
        }
        break;
    }
  }

  @Override
  public void sendLocalized(@NonNull String key) {
    if (this.type == LinkableType.MINECRAFT) {
      this.sendLocalized(key, new HashMap<>());
    } else {
      this.sendMessage(Guido.getHandlers().getLanguageHandler().getFile(this).get(key));
    }
  }

  @Override
  public void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders) {
    if (this.type == LinkableType.MINECRAFT) {
      JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
      if (bungee != null) {
        Requests.Bungee.sendLocalized(this.requireMinecraftRef().getUuid(), key, placeholders)
            .queue(bungee);
      }
    } else {
      this.sendMessage(
          Guido.getHandlers().getLanguageHandler().getFile(this).get(key, placeholders));
    }
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
    return Guido.getHandlers().getLoader().getTeams().getTeam(this);
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
    return (GuidoLinkable) BotCatchable.super.cache();
  }

  @Override
  public boolean addPermission(
      @NonNull String context, @NonNull String node, boolean enabled, long expires) {
    boolean added = Linkable.super.addPermission(context, node, enabled, expires);
    if (added) new PermissiblePermissionAddedEvent(this, context, node, enabled, expires).call();
    return added;
  }

  @Override
  public boolean removePermission(@NonNull String context, @NonNull String node) {
    boolean removed = Linkable.super.removePermission(context, node);
    if (removed) new PermissiblePermissionRemovedEvent(this, context, node).call();
    return removed;
  }

  @NonNull
  @Override
  public Collection<Linkable> getLinks() {
    UserData user = this.getLinkedUser();
    if (user != null) {
      return Guido.getHandlers().getLoader().getLinks().getLinks(user);
    }
    return new HashSet<>();
  }

  @NonNull
  @Override
  public String getSingle() {
    switch (this.getType()) {
      case DISCORD:
        User user = this.requireDiscordRef().getUser(Guido.getConnection().validatedJda());
        return user != null ? user.getAsMention() : "invalid";
      case MINECRAFT:
        return this.getRecognition().getOr("nickname", String.class, "invalid");
      default:
        throw new IllegalArgumentException(this.getType() + " is not a valid type");
    }
  }

  @NonNull
  @Override
  @Deprecated
  public String getReadable(@NonNull LocaleFile locale) {
    switch (this.getType()) {
      case DISCORD:
        User user = this.requireDiscordRef().getUser(Guido.getConnection().validatedJda());
        if (user != null) {
          return locale.get("link.discord", Maps.builder("mention", user.getAsMention()));
        } else {
          return locale.get("link.invalid");
        }
      case MINECRAFT:
        String nickname = this.getIdentification().get("nickname", String.class);
        if (nickname != null) {
          return locale.get("link.minecraft", Maps.singleton("nickname", nickname));
        } else {
          return locale.get("link.invalid");
        }
      default:
        throw new IllegalArgumentException(this.getType() + " is not a valid type");
    }
  }

  @Override
  public UserData getLinkedUser() {
    return Guido.getHandlers().getLoader().getUsers().getUserData(this.getLinkedUserId());
  }
}
