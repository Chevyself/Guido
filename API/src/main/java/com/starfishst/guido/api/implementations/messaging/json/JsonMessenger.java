package com.starfishst.guido.api.implementations.messaging.json;

import com.google.gson.JsonObject;
import com.starfishst.core.utils.Strings;
import com.starfishst.guido.api.implementations.messaging.AwaitingRequest;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import com.starfishst.guido.api.implementations.messaging.exception.MessengerListenFailException;
import com.starfishst.utils.gson.GsonProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link com.starfishst.guido.api.implementations.messaging.Messenger} that works with json
 * messages
 */
public interface JsonMessenger extends Messenger {

  /**
   * Prints a line in the output stream
   *
   * @param line the line to print
   */
  default void printLine(@NotNull String line) {
    this.getOutput().println(line);
    this.getOutput().flush();
  }

  /**
   * Get a request using the id of it
   *
   * @param id the id of the request
   * @return the request if found else null
   */
  @Nullable
  default AwaitingRequest<?> getRequest(@NotNull UUID id) {
    for (AwaitingRequest<?> awaitingRequest : this.getRequests().keySet()) {
      if (awaitingRequest.getRequest().getId().equals(id)) {
        return awaitingRequest;
      }
    }
    return null;
  }

  /**
   * Get a responsive giver using the method requested
   *
   * @param method the method requested
   * @return the response giver if there is one for a method else null
   */
  @Nullable
  default ResponseGiver<?> getResponseGiver(@NotNull String method) {
    return this.getResponseGivers().get(method);
  }

  /**
   * Get the output line to send messages
   *
   * @return the output line to send messages
   */
  @NotNull
  PrintWriter getOutput();

  /**
   * Get the input line to receive messages
   *
   * @return the input line
   */
  @NotNull
  BufferedReader getInput();

  /**
   * Get the response givers for this messenger
   *
   * @return the response givers
   */
  @NotNull
  HashMap<String, ResponseGiver<?>> getResponseGivers();

  /**
   * Get the requests in a map that contains them and the time since they were requested
   *
   * @return the HashMap of request and the time
   */
  @NotNull
  HashMap<AwaitingRequest<?>, Long> getRequests();

  /**
   * Get when request may timeout
   *
   * @return the time to timeout in millis
   */
  long getTimeout();

  /**
   * Whether this messenger has stopped listening
   *
   * @return true if the messenger is no longer listening
   */
  boolean isStopped();

  @Override
  default <T> void sendRequest(
      @NotNull Request request, @NotNull Class<T> clazz, @NotNull Consumer<T> consumer) {
    this.getRequests()
        .put(new AwaitingRequest<>(request, clazz, consumer), System.currentTimeMillis());
    this.printLine(GsonProvider.GSON.toJson(request));
  }

  @Override
  default void sendRequest(@NotNull VoidRequest request) {
    this.printLine(GsonProvider.GSON.toJson(request));
  }

  @Override
  default void listen() throws MessengerListenFailException {
    if (this.isStopped()) return;
    try {
      StringBuilder builder = Strings.getBuilder();
      while (this.getInput().ready()) {
        String line = this.getInput().readLine();
        builder.append(line).append("\n");
      }
      if (builder.length() != 0) {
        JsonObject object = GsonProvider.GSON.fromJson(builder.toString(), JsonObject.class);
        if (object.get("method") != null) {
          Request request = GsonProvider.GSON.fromJson(object, Request.class);
          ResponseGiver<?> giver =
              this.getResponseGiver(
                  request.getMethod().startsWith("void")
                      ? request.getMethod().substring(4)
                      : request.getMethod());
          if (giver != null && !request.getMethod().startsWith("void")) {
            this.printLine(GsonProvider.GSON.toJson(giver.getResponse(request, this)));
          } else if (giver != null) {
            giver.getResponse(request, this);
          }
        } else if (object.get("object") != null) {
          AwaitingRequest<?> awaitingRequest =
              this.getRequest(UUID.fromString(object.get("id").getAsString()));
          if (awaitingRequest != null) {
            awaitingRequest
                .getConsumer()
                .accept(
                    GsonProvider.GSON.fromJson(
                        object.get("object"), (Type) awaitingRequest.getClazz()));
            this.getRequests().remove(awaitingRequest);
          }
        }
      }
    } catch (IOException e) {
      throw new MessengerListenFailException(null, e);
    }
    Set<AwaitingRequest<?>> toRemove = new HashSet<>();
    this.getRequests()
        .forEach(
            (request, start) -> {
              if (System.currentTimeMillis() - start > this.getTimeout()) {
                toRemove.add(request);
              }
            });
    if (!toRemove.isEmpty()) {
      throw new MessengerListenFailException("Requests timed out! \n " + toRemove);
    }
    try {
      Thread.sleep(100);
    } catch (InterruptedException ignored) {
    }
  }
}
