package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.GuildData;
import com.starfishst.guido.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when guild data has been loaded
 */
public class GuildDataLoadedEvent extends GuildDataEvent {

    /**
     * Create the event
     *
     * @param data the guild data that has been loaded
     */
    public GuildDataLoadedEvent(@NotNull GuildData data) {
        super(data);
    }

}
