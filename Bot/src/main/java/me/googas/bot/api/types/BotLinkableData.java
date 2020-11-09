package me.googas.bot.api.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.LinkableData;
import me.googas.api.links.LinkableDataType;
import me.googas.api.user.UserData;
import me.googas.bot.core.Guido;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An extension for linked data for bot use */
public interface BotLinkableData extends BotPermissible, LinkableData, BotCatchable {

  /**
   * Get the discord member for the linked data. The type must be {@link
   * LinkableDataType#DISCORD_GUILD} else it will return null
   *
   * @return the discord member
   * @param guildId the id of the guild which needs the member
   */
  @Nullable
  default Member getDiscordMember(long guildId) {
    for (LinkableData link : this.getLinks(LinkableDataType.DISCORD_GUILD)) {
      if (link instanceof BotLinkableData) {
        long id = link.getIdentification().getOr("id", Long.class, -1L);
        long linkGuild = link.getIdentification().getOr("guild", Long.class, -1L);
        if (linkGuild == guildId) {
          Guild guild = Guido.getConnection().validatedJda().getGuildById(guildId);
          if (guild != null) {
            return guild.getMemberById(id);
          }
        }
      }
    }
    return null;
  }

  /**
   * Get the discord user of the data. The {@link #getType()} must be {@link
   * LinkableDataType#DISCORD} or {@link LinkableDataType#DISCORD_GUILD} in order to get it else
   * null will be returned this applies if the user cannot be reached by the bot
   *
   * @return the discord user
   */
  @Nullable
  default User getDiscordUser() {
    if (this.getType() == LinkableDataType.DISCORD
        || this.getType() == LinkableDataType.DISCORD_GUILD) {
      return Guido.getConnection()
          .validatedJda()
          .getUserById(this.getIdentification().getOr("id", Long.class, -1L));
    }
    return null;
  }

  @Override
  @Nullable
  default UserData getLinkedUser() {
    return Guido.getDataLoader().getUserData(this.getLinkedUserId());
  }

  /**
   * If this linked data happens to be {@link LinkableDataType#MINECRAFT} this will return the uuid
   * of the user if it is not invalid. If it is something else it will search for minecraft links if
   * it finds one it will be returned
   *
   * @return the unique id of the
   */
  @Nullable
  default UUID getUniqueId() {
    if (this.getType() == LinkableDataType.MINECRAFT) {
      String trimmed = this.getIdentification().get("uuid", String.class);
      if (trimmed != null) {
        try {
          return UUIDUtils.untrim(trimmed);
        } catch (IllegalArgumentException e) {
          return null;
        }
      }
    }
    return null;
  }

  @NotNull
  @Override
  default String getReadable(@NotNull LocaleFile locale) {
    switch (this.getType()) {
      case DISCORD_GUILD:
      case DISCORD:
        User user = this.getDiscordUser();
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

  @NotNull
  @Override
  default String getSingle() {
    switch (this.getType()) {
      case DISCORD_GUILD:
      case DISCORD:
        User user = this.getDiscordUser();
        return user != null ? user.getAsMention() : "invalid";
      case MINECRAFT:
        return this.getIdentification().getOr("nickname", String.class, "invalid");
      default:
        throw new IllegalArgumentException(this.getType() + " is not a valid type");
    }
  }

  @Override
  @NotNull
  BotLinkableInfo getInfo();

  @NotNull
  @Override
  default Collection<LinkableData> getLinks() {
    UserData user = this.getLinkedUser();
    if (user != null) {
      return Guido.getDataLoader().getLinks(user);
    }
    return new HashSet<>();
  }

  @NotNull
  @Override
  default Collection<LinkableData> getLinks(@NotNull LinkableDataType... types) {
    UserData user = this.getLinkedUser();
    if (user != null) {
      return Guido.getDataLoader().getLinks(user, types);
    }
    return new HashSet<>();
  }
}
