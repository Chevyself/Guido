package com.starfishst.guido.data;

import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.GuildData;
import com.starfishst.guido.api.events.data.GuildDataLoadedEvent;
import com.starfishst.guido.api.events.data.GuildDataUnloadedEvent;

/**
 * This object represents the data for a guild that is using this bot
 */
public class GuidoGuild extends Catchable implements GuildData {

    /**
     * The unique id of the guild
     */
    private transient final long id;

    /**
     * Create the guido guild
     *
     * @param id the id of the guild
     */
    public GuidoGuild(long id) {
        super(Time.fromString("3m"));
        this.id = id;
        new GuildDataLoadedEvent(this).call();
    }

    /**
     * Create the guido guild. Deprecated because this type of constructor is required for GSON
     */
    @Deprecated
    public GuidoGuild() {
        this(0);
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void onSecondsPassed() {

    }

    @Override
    public void onRemove() {
        new GuildDataUnloadedEvent(this).call();
    }

}