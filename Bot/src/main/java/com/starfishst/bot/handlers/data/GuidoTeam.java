package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.guido.api.data.matches.Team;
import com.starfishst.guido.api.data.matches.TeamRole;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation for team
 */
public class GuidoTeam implements Team {

    /**
     * The members of the team
     */
    @NotNull
    private final HashMap<BotUser, TeamRole> members;

    /**
     * Create the team
     *
     * @param members the members inside the team
     */
    public GuidoTeam(@NotNull HashMap<BotUser, TeamRole> members) {
        this.members = members;
    }

    @Override
    public @NotNull Map<BotUser, TeamRole> getMembers() {
        return this.members;
    }
}
