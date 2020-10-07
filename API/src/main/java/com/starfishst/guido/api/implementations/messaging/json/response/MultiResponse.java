package com.starfishst.guido.api.implementations.messaging.json.response;

import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.starfishst.guido.api.data.token.AuthLevel;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.json.JsonMessenger;
import java.util.ArrayList;
import java.util.List;
import me.googas.commons.gson.GsonProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A response for an stack of requests */
public class MultiResponse implements ResponseGiver<List<Response<?>>> {

  /** The messenger using this response */
  @NotNull private final JsonMessenger messenger;

  /**
   * Create the response
   *
   * @param messenger the messenger using this response
   */
  public MultiResponse(@NotNull JsonMessenger messenger) {
    this.messenger = messenger;
  }

  @Override
  public @Nullable Response<List<Response<?>>> getResponse(
      @NotNull Request<?> queryRequest, @NotNull Messenger messenger) {
    List<LinkedTreeMap<?, ?>> requests = queryRequest.getListParameter("requests");
    List<Response<?>> responses = new ArrayList<>();
    for (LinkedTreeMap<?, ?> requestMap : requests) {
      JsonObject object = GsonProvider.GSON.toJsonTree(requestMap).getAsJsonObject();
      Request request = GsonProvider.GSON.fromJson(object, Request.class);
      ResponseGiver<?> giver = this.messenger.getResponseGiver(request);
      if (giver != null) {
        Response<?> response = giver.getResponse(request, this.messenger);
        if (response != null && !request.getMethod().startsWith("void")) {
          responses.add(response);
        }
      }
    }
    return new Response<>(queryRequest.getId(), responses);
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return AuthLevel.NONE;
  }
}
