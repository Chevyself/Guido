package com.starfishst.guido.api.data.matches;

import com.starfishst.guido.api.data.UserData;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This object represents a team. Which is basically a collection of
 * members
 */
public interface Team {

    /**
     * Get the members of the team
     * @return the members of the team
     */
    @NotNull Map<? extends UserData, TeamRole> getMembers();

}
