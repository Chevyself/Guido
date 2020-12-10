package me.googas.api.discord;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.matches.GlobalLadder;
import me.googas.api.matches.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.commons.cache.Catchable;

/** This object represents the data for a guild */
public interface GuildData extends Catchable {

  /**
   * Get the unique id of the guild. This is an object in discord that must have its unique id
   *
   * @return the unique id of the guild
   */
  long getId();

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
   * This map contains the string to identify a channel and its category
   *
   * @return the map of categories
   */
  @NonNull
  Map<String, Long> getCategories();
}
