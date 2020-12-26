package me.googas.bot.api.types.links;

import java.util.Collection;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.user.UserData;
import me.googas.bot.Guido;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.api.types.permissions.BotPermissible;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/** An extension for linked data for bot use */
public interface BotLinkable extends BotPermissible, Linkable, BotCatchable {

  /**
   * Get the discord member for the linked data. The type must be {@link LinkableType#DISCORD_GUILD}
   * else it will return null
   *
   * @return the discord member
   * @param guildId the id of the guild which needs the member
   */
  default Member getDiscordMember(long guildId) {
    for (Linkable link : this.getLinks(LinkableType.DISCORD_GUILD)) {
      if (link instanceof BotLinkable) {
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
   * Get the discord user of the data. The {@link #getType()} must be {@link LinkableType#DISCORD}
   * or {@link LinkableType#DISCORD_GUILD} in order to get it else null will be returned this
   * applies if the user cannot be reached by the bot
   *
   * @return the discord user
   */
  default User getDiscordUser() {
    if (this.getType() == LinkableType.DISCORD || this.getType() == LinkableType.DISCORD_GUILD) {
      return Guido.getConnection()
          .validatedJda()
          .getUserById(this.getIdentification().getOr("id", Long.class, -1L));
    }
    return null;
  }

  @Override
  default UserData getLinkedUser() {
    return Guido.getDataLoader().getUserData(this.getLinkedUserId());
  }

  @NonNull
  @Override
  default String getReadable(@NonNull LocaleFile locale) {
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

  @NonNull
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
  @NonNull
  BotLinkableInfo getInfo();

  @NonNull
  @Override
  default Collection<Linkable> getLinks() {
    UserData user = this.getLinkedUser();
    if (user != null) {
      return Guido.getDataLoader().getLinks(user);
    }
    return new HashSet<>();
  }

  @NonNull
  @Override
  default Collection<Linkable> getLinks(@NonNull LinkableType... types) {
    UserData user = this.getLinkedUser();
    if (user != null) {
      return Guido.getDataLoader().getLinks(user, types);
    }
    return new HashSet<>();
  }
}
