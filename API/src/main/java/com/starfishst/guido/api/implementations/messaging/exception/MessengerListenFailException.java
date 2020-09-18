package com.starfishst.guido.api.implementations.messaging.exception;

import com.starfishst.guido.api.implementations.messaging.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown when the {@link com.starfishst.guido.api.implementations.messaging.Messenger} fails to
 * {@link Messenger#listen()}
 */
public class MessengerListenFailException extends Exception {

  /**
   * Create the exception
   *
   * @param message the message to why it failed
   */
  public MessengerListenFailException(@Nullable String message) {
    super(message);
  }

  /**
   * Create the exception
   *
   * @param message the message to why it failed
   * @param cause the throwable that made if fail
   */
  public MessengerListenFailException(@Nullable String message, @NotNull Throwable cause) {
    super(message, cause);
  }

  /**
   * Create the exception
   *
   * @param cause the throwable that made if fail
   */
  public MessengerListenFailException(@NotNull Throwable cause) {
    super(cause);
  }
}
