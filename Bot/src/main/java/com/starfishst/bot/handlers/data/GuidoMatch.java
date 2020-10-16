package com.starfishst.bot.handlers.data;

import com.starfishst.guido.api.data.matches.Match;
import com.starfishst.guido.api.data.matches.Team;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * An implementation for a match
 */
public class GuidoMatch extends Catchable implements Match {

    @NotNull
    private final String id;

    @NotNull
    private final Set<GuidoTeam> teams;
    @NotNull
    private final GuidoValuesMap details;
    @Nullable
    private GuidoTeam winners;

    public GuidoMatch(@NotNull String id, @NotNull Set<GuidoTeam> teams, @Nullable GuidoTeam winners, @NotNull GuidoValuesMap details) {
        this.id = id;
        this.teams = teams;
        this.winners = winners;
        this.details = details;
    }

    @Override
    public void onSecondPassed() {

    }

    @Override
    public void onRemove() {

    }

    @Override
    public @NotNull String getId() {
        return null;
    }

    @Override
    public @NotNull Collection<Team> getTeams() {
        return null;
    }

    @Override
    public @Nullable GuidoTeam getWinners() {
        return this.winners;
    }

    @Override
    public @NotNull GuidoValuesMap getDetails() {
        return null;
    }
}
