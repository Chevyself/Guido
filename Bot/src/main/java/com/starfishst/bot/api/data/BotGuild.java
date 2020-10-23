package com.starfishst.bot.api.data;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.data.GuidoLadder;
import com.starfishst.bot.handlers.data.GuidoRankRange;
import com.starfishst.guido.api.data.discord.GuildData;
import com.starfishst.guido.api.data.matches.Ladder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/** The data of a guild in jda */
public interface BotGuild extends GuildData {

  @NotNull
  @Override
  Collection<GuidoLadder> getLadders();

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
   * Get the roles for certain ladder and
   *
   * @param ladder the ladder that represents those roles
   * @param numb whether to get the roles inside or outside bounds. if what to get outside * this
   *     must be true
   * @param outside whether to get the roles inside or outside bounds. if what to get outside * this
   *     must be true
   * @return the roles that are representative for the ladder and the number inside or outside
   *     bounds
   */
  default Collection<Role> getRolesDiscord(@NotNull Ladder ladder, int numb, boolean outside) {
    return this.getRolesById(this.getRoles(ladder, numb, outside));
  }

  /**
   * Get the global roles for the given number
   *
   * @param numb the number to be in or off bounds of the range
   * @param outside whether to get the roles inside or outside bounds. if what to get outside this
   *     must be true
   * @return the global roles
   */
  default Collection<Role> getGlobalRolesDiscord(int numb, boolean outside) {
    return this.getRolesById(this.getGlobalRoles(numb, outside));
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
    for (Long id : rolesId) {
      Role role = guild.getRoleById(id);
      if (role != null) {
        roles.add(role);
      } else {
        // TODO remove it
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
        "Guild in " + this.getId() + " no longer exists");
  }

  @Override
  @NotNull
  Map<Long, GuidoRankRange> getRanges();
}
