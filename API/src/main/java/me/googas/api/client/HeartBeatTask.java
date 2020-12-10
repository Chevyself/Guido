package me.googas.api.client;

import java.io.IOException;
import lombok.NonNull;

/** This task attempts to reconnect to the bot */
public interface HeartBeatTask extends Runnable {

  /** Called when the beat task is successful */
  void onSuccess();

  /**
   * Called when the beat task ends with an exception
   *
   * @param exception the exception that caused the task to fail
   */
  void onError(@NonNull Throwable exception);

  /**
   * Get the client that is attempting to reconnect
   *
   * @return the client instance
   */
  @NonNull
  Client getClient();

  @Override
  default void run() {
    if (this.getClient().getConnection() != null) return;
    try {
      this.getClient().startConnection();
      this.onSuccess();
    } catch (IOException e) {
      this.onError(e);
    }
  }
}
