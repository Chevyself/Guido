package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.RoleData;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the data of a role gets loaded
 */
public class RoleDataLoadedEvent extends RoleDataEvent {
    /**
     * Create the event
     *
     * @param data the role data involved in the event
     */
    public RoleDataLoadedEvent(@NotNull RoleData data) {
        super(data);
    }
}
