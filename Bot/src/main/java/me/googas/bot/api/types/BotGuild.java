package me.googas.bot.api.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.discord.GuildData;
import me.googas.api.matches.Ladder;
import me.googas.bot.core.Guido;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

/**
 * The data of a guild in jda TODO the api does not need guild or roles they should be moved to just
 * be used in here
 */
public interface BotGuild extends GuildData, BotCatchable {

  /**
   * Get the discord text channel for the given key
   *
   * @param key the key to get the channel
   * @return the channel
   */
  @NotNull
  default TextChannel getChannel(@NotNull String key) {
    Guild guild = this.getDiscord();
    TextChannel channel = guild.getTextChannelById(this.getChannels().getOrDefault(key, -1L));
    if (channel == null) {
      channel = guild.createTextChannel(key).complete();
      this.getChannels().put(key, channel.getIdLong());
    }
    return channel;
  }

  /**
   * Get the discord text channel for the given key
   *
   * @param key the key to get the channel
   * @return the channel
   */
  @NotNull
  default VoiceChannel getVoiceChannel(@NotNull String key) {
    Guild guild = this.getDiscord();
    VoiceChannel channel =
        guild.getVoiceChannelById(this.getVoiceChannels().getOrDefault(key, -1L));
    if (channel == null) {
      channel = guild.createVoiceChannel(key).complete();
      this.getVoiceChannels().put(key, channel.getIdLong());
    }
    return channel;
  }

  /**
   * Get the discord category for the given key
   *
   * @param key the key to get the category
   * @return the category
   */
  @NotNull
  default Category getCategory(@NotNull String key) {
    Guild guild = this.getDiscord();
    Category category = guild.getCategoryById(this.getCategories().getOrDefault(key, -1L));
    if (category == null) {
      category = guild.createCategory(key).complete();
      this.getChannels().put(key, category.getIdLong());
    }
    return category;
  }

  /**
   * Get the roles for certain ladder and
   *
   * @param ladder the ladder that represents those roles
   * @param numb whether to get the roles inside or outside bounds
   * @param bounds whether to get the roles inside or outside bounds. if what to get inside this
   *     must be true
   * @return the roles that are representative for the ladder and the number inside or outside
   *     bounds
   */
  default Collection<Role> getRolesDiscord(@NotNull Ladder ladder, int numb, boolean bounds) {
    return this.getRolesById(this.getRoles(ladder, numb, bounds));
  }

  /**
   * Get the global roles for the given number
   *
   * @param numb the number to be in or off bounds of the range
   * @param bounds whether to get the roles inside or outside bounds. if what to get in bounds this
   *     must be true
   * @return the global roles
   */
  default Collection<Role> getGlobalRolesDiscord(int numb, boolean bounds) {
    return this.getRolesById(this.getGlobalRoles(numb, bounds));
  }

  /**
   * Get all the roles from a given collection of ids
   *
   * @param rolesId the ids of the roles
   * @return the roles given from the id
   */
  @NotNull
  default Collection<Role> getRolesById(@NotNull Collection<Long> rolesId) {
    Guild guild = this.getDiscord();
    Set<Role> roles = new HashSet<>();
    for (long id : rolesId) {
      Role role = guild.getRoleById(id);
      if (role != null) {
        roles.add(role);
      }
    }

    return roles;
  }

  /**
   * Get the data as a discord guild
   *
   * @return the discord guild
   */
  @NotNull
  default Guild getDiscord() {
    return Validate.notNull(
        Guido.getConnection().validatedJda().getGuildById(this.getId()),
        "Seems like the guild with the id " + this.getId() + " no longer exists");
  }
}
