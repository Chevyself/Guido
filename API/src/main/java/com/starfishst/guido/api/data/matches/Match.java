package com.starfishst.guido.api.data.matches;

import com.starfishst.guido.api.data.ValuesMap;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This object represents a match which was played by one ore more teams
 */
public interface Match extends ICatchable {

    /**
     * The unique id of the match
     *
     * @return the unique id of the match
     */
    @NotNull
    String getId();

    /**
     * Get the teams that are participating in the match
     *
     * @return collection of teams
     */
    @NotNull Collection<? extends Team> getTeams();

    /**
     * Get the team that won the match
     *
     * @return the team that won the match. This can return null
     * in case that the match has not finished yet
     */
    @Nullable
    Team getWinners();

    /**
     * Get the details of the match
     *
     * @return the details of the match
     */
    @NotNull
    ValuesMap getDetails();
}
