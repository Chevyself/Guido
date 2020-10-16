package com.starfishst.guido.api.data;

import org.jetbrains.annotations.NotNull;

/**
 * A rank range is a range for certain elo rank
 */
public interface RankRange extends Range {

    /**
     * Get the ladder where this range applies
     *
     * @return the ladder where this range applies
     */
    @NotNull
    String getLadder();
}
