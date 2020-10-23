package com.starfishst.guido.api.data.discord;

import com.starfishst.guido.api.data.RankRange;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.matches.Ladder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents the data for a guild */
public interface GuildData extends ICatchable {

  /**
   * Get the unique id of the guild. This is an object in discord that must have its unique id
   *
   * @return the unique id of the guild
   */
  long getId();

  /**
   * Get the multipliers for stuff inside the guild.
   *
   * <p>Those multipliers can be used in a lot of stuff mainly used to set the elo in ladders
   *
   * @return the map of multipliers
   */
  @NotNull
  Map<String, Integer> getMultipliers();

  /**
   * Get a ladder using its name
   *
   * @param name the name of the ladder
   * @return the ladder if found else null
   */
  @Nullable
  default Ladder getLadder(@NotNull String name) {
    if (name.equalsIgnoreCase("global")) {
      return new Ladder() {
        @Override
        public int playersPerTeam() {
          return -1;
        }

        @Override
        public int baseValue() {
          return -1;
        }

        @Override
        public @NotNull String getName() {
          return "global";
        }

        @Override
        public @NotNull ValuesMap getOptions() {
          return HashMap::new;
        }
      };
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
  default Collection<Long> getRoles(@NotNull Ladder ladder, int numb, boolean outside) {
    Set<Long> rolesId = new HashSet<>();
    this.getRanges()
        .forEach(
            (id, range) -> {
              if (!range.isBound(numb)
                  && outside
                  && range.getLadder().equalsIgnoreCase(ladder.getName())) {
                rolesId.add(id);
              } else if (range.isBound(numb)
                  && !outside
                  && range.getLadder().equalsIgnoreCase(ladder.getName())) {
                rolesId.add(id);
              }
            });
    return rolesId;
  }

  /**
   * Get the global roles for the given number
   *
   * @param numb the number to be in or off bounds of the range
   * @param outside whether to get the roles inside or outside bounds. if what to get outside this
   *     must be true
   * @return the global roles
   */
  default Collection<Long> getGlobalRoles(int numb, boolean outside) {
    Set<Long> rolesId = new HashSet<>();
    this.getRanges()
        .forEach(
            (id, range) -> {
              if (range.isBound(numb) && range.getLadder().equalsIgnoreCase("global")) {
                rolesId.add(id);
              }
            });
    return rolesId;
  }

  /**
   * This map contains the ids of roles and it's respective rank range. This is used to give roles
   * in certain ladders when someone reaches certain elo
   *
   * @return the ranges
   */
  @NotNull
  Map<Long, ? extends RankRange> getRanges();

  /**
   * Get the ladders of the guild and the ladder base value
   *
   * @return the map of the ladders and its initial base value
   */
  @NotNull
  Collection<? extends Ladder> getLadders();

  /**
   * This map contains the string to identify a channel and its id
   *
   * @return the map of channels
   */
  @NotNull
  Map<String, Long> getChannels();

  /**
   * This map contains the string to identify a channel and its category
   *
   * @return the map of categories
   */
  @NotNull
  Map<String, Long> getCategories();
}
