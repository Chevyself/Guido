package com.starfishst.bukkit.dependencies.pgm;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.Validate;

import java.lang.ref.SoftReference;

public class PGMLeader {

    @NonNull @Getter @Delegate
    private final SoftReference<TeamMember> captain;

    public PGMLeader(@NonNull TeamMember captain) {
        if (captain.getTeamRole() != TeamRole.LEADER) throw new IllegalArgumentException(captain + " is not a team leader");
        this.captain = new SoftReference<>(captain);
    }

    @NonNull @Delegate
    public TeamMember validated() {
        return Validate.notNull(this.captain.get(), "Reference to captain has expired");
    }

}
