package com.starfishst.bukkit.matches;

import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.ValuesMap;

import java.util.Set;

public class HostedMatch {

    /**
     * This is the id that represents in {@link me.googas.api.matches.Match}
     */
    @NonNull @Getter
    private final String id;

    /**
     * The list of participants that are playing in the match. This list should not be modified unless it
     * is really required
     */
    @NonNull @Getter
    private final Set<HostedPlayer> participants;

    /** The ladder which is being played */
    @Nullable @Getter
    private final String ladder;

    /** The details of the match */
    @NonNull @Getter private final ValuesMap details;

    public HostedMatch(@NonNull String id, @NonNull Set<HostedPlayer> participants, @Nullable String ladder, @NonNull ValuesMap details) {
        this.id = id;
        this.participants = participants;
        this.ladder = ladder;
        this.details = details;
    }
}
