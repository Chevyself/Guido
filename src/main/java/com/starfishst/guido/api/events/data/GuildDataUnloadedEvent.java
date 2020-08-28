package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.GuildData;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the guild data is unloaded
 */
public class GuildDataUnloadedEvent extends GuildDataEvent {

    /**
     * Create the event
     *
     * @param data the guild data that has been loaded
     */
    public GuildDataUnloadedEvent(@NotNull GuildData data) {
        super(data);
    }

}
