package com.starfishst.guido.api.events;

import com.starfishst.guido.Guido;
import com.starfishst.utils.events.Cancellable;
import com.starfishst.utils.events.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents an event that can be cancelled
 */
public interface GuidoCancellable extends Cancellable {

    /**
     * Calls an event. As in {@link GuidoEvent#call()} but returns whether it was cancelled
     *
     * @return true if the event was cancelled
     * @throws IllegalArgumentException cancellable is not an instance of {@link Event}
     */
    default boolean call() {
        return Guido.call(this);
    }

}
