package me.googas.api.utility;

import lombok.NonNull;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Static utilities for requests
 */
public class Requests {

    @NonNull
    public static <T> Consumer<Optional<T>> ifPresentElse(@NonNull Consumer<T> ifPresent, @NonNull Runnable elseRun) {
        return optional -> {
            if (optional.isPresent()) {
                ifPresent.accept(optional.get());
            } else {
                elseRun.run();
            }
        };
    }
}
