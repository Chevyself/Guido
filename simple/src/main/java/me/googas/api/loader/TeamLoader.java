package me.googas.api.loader;

import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.matches.team.Team;
import me.googas.commons.RandomUtils;

public interface TeamLoader extends DataLoader {

  /**
   * Get a team by its id
   *
   * @param id the id of the team
   * @return the team if found else null
   */
  Team getTeam(@NonNull String id);

  /**
   * Get a team by its name
   *
   * @param name the name of the team
   * @return the team if found else null
   */
  Team getTeamByName(@NonNull String name);

  /**
   * Get the team in which a linkable is on
   *
   * @param linkable the linkable to getId the team
   * @return the team if found else null
   */
  Team getTeam(@NonNull Linkable linkable);

  /**
   * Get a new id fr a team
   *
   * @return the new id fr the team
   */
  @NonNull
  default String nextTeamId() {
    String id = RandomUtils.nextString(6);
    if (this.getTeam(id) != null) return this.nextTeamId();
    return id;
  }

  /**
   * Delete a team by using its id
   *
   * @param id the id of the team to delete
   * @return whether the team was deleted
   */
  boolean deleteTeam(@NonNull String id);
}
