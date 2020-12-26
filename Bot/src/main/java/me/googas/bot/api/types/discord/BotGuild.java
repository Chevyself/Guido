package me.googas.bot.api.types.discord;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.bot.Guido;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

/** The data of a guild in jda */
public interface BotGuild extends BotCatchable {

  /**
   * Get a ladder using its name
   *
   * @param name the name of the ladder
   * @return the ladder if found else null
   */
  default Ladder getLadder(@NonNull String name) {
    if (name.equalsIgnoreCase("global")) {
      return GlobalLadder.INSTANCE;
    } else {
      for (Ladder ladder : this.getLadders()) {
        if (ladder.getName().equalsIgnoreCase(name)) {
          return ladder;
        }
      }
      return null;
    }
  }

  /**
   * Get the roles for certain ladder
   *
   * @param ladder the ladder that represents those roles
   * @param numb whether to get the roles inside or outside bounds. if what to get outside * this
   *     must be true
   * @param bounds whether to get the roles inside or outside bounds. if what to get in bounds *
   *     this must be true
   * @return the roles that are representative for the ladder and the number inside or outside
   *     bounds
   */
  default Collection<Long> getRoles(@NonNull Ladder ladder, int numb, boolean bounds) {
    return this.getRoles(ladder.getName(), numb, bounds);
  }

  /**
   * Get the roles for certain ladder
   *
   * @param ladder the name of the ladder that represents those roles
   * @param numb whether to get the roles inside or outside bounds. if what to get outside * this
   *     must be true
   * @param bounds whether to get the roles inside or outside bounds. if what to get in bounds *
   *     this must be true
   * @return the roles that are representative for the ladder and the number inside or outside
   *     bounds
   */
  default Collection<Long> getRoles(@NonNull String ladder, int numb, boolean bounds) {
    Set<Long> rolesId = new HashSet<>();
    this.getRanges()
        .forEach(
            (id, range) -> {
              if (!range.getLadder().equalsIgnoreCase(ladder)) return;
              if (range.isBound(numb) && bounds) {
                rolesId.add(id);
              } else if (!range.isBound(numb) && !bounds) {
                rolesId.add(id);
              }
            });
    return rolesId;
  }

  /**
   * Get the global roles for the given number
   *
   * @param numb the number to be in or off bounds of the range
   * @param bounds whether to get the roles inside or outside bounds. if what to get is bounds this
   *     must be true
   * @return the global roles
   */
  default Collection<Long> getGlobalRoles(int numb, boolean bounds) {
    return this.getRoles("global", numb, bounds);
  }

  /**
   * Get a message by its id
   *
   * @param id the id of the message to get
   * @return the message if found else null
   */
  ResponsiveMesage getMessage(long id);

  /**
   * Get the unique id of the guild. This is an object in discord that must have its unique id
   *
   * @return the unique id of the guild
   */
  long getId();

  /**
   * This map contains the ids of roles and it's respective rank range. This is used to give roles
   * in certain ladders when someone reaches certain elo
   *
   * @return the ranges
   */
  @NonNull
  Map<Long, RankRange> getRanges();

  /**
   * Get the ladders of the guild and the ladder base value
   *
   * @return the map of the ladders and its initial base value
   */
  @NonNull
  Collection<Ladder> getLadders();

  /**
   * This map contains the string to identify a channel and its id
   *
   * @return the map of channels
   */
  @NonNull
  Map<String, Long> getChannels();

  /**
   * This map contains the string to identify a voice channel and its id
   *
   * @return the map of channels
   */
  @NonNull
  Map<String, Long> getVoiceChannels();

  /**
   * Get the discord text channel for the given key
   *
   * @param key the key to get the channel
   * @return the channel
   */
  @NonNull
  default TextChannel getTextChannel(@NonNull String key) {
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
  @NonNull
  default VoiceChannel getVoiceChannel(@NonNull String key) {
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
  @NonNull
  default Category getCategory(@NonNull String key) {
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
  default Collection<Role> getRolesDiscord(@NonNull Ladder ladder, int numb, boolean bounds) {
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
  @NonNull
  default Collection<Role> getRolesById(@NonNull Collection<Long> rolesId) {
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
   * Get the key of a voice channel matching the id
   *
   * @param id the id of the channel to match
   * @return the key that contains that channel if found else null
   */
  default String getVoiceChannel(long id) {
    AtomicReference<String> atomic = new AtomicReference<>();
    this.getVoiceChannels()
        .forEach(
            (key, channel) -> {
              if (channel == id) atomic.set(key);
            });
    return atomic.get();
  }

  /**
   * This map contains the string to identify a channel and its category
   *
   * @return the map of categories
   */
  @NonNull
  Map<String, Long> getCategories();

  /**
   * Get the responsive messages of the server
   *
   * @return the responsive messages
   */
  @NonNull
  Collection<ResponsiveMesage> getMessages();

  /**
   * Get the data as a discord guild
   *
   * @return the discord guild
   */
  @NonNull
  default Guild getDiscord() {
    return Validate.notNull(
        Guido.getConnection().validatedJda().getGuildById(this.getId()),
        "Seems like the guild with the id " + this.getId() + " no longer exists");
  }
}
