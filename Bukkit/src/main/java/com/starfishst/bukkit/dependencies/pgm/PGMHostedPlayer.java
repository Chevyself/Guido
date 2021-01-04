package com.starfishst.bukkit.dependencies.pgm;

import com.starfishst.bukkit.matches.HostedPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.Validate;

import java.lang.ref.SoftReference;

public class PGMHostedPlayer {

    @NonNull
    @Getter
    @Delegate
    private final SoftReference<HostedPlayer> player;

    public PGMHostedPlayer(@NonNull HostedPlayer player) {
        this.player = new SoftReference<>(player);
    }

    @NonNull @Delegate
    public HostedPlayer validated() {
        return Validate.notNull(this.player.get(), "Reference to captain has expired");
    }
}
