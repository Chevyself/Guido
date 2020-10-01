package com.starfishst.guido.api.implementations.messaging.json.response;

import com.starfishst.guido.api.Ping;
import com.starfishst.guido.api.data.AuthLevel;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A request for a {@link Ping} request */
public class PingResponse implements ResponseGiver<Ping> {

  @Override
  public @Nullable Response<Ping> getResponse(
      @NotNull Request<?> request, @NotNull Messenger messenger) {
    double millis =
        System.currentTimeMillis()
            - request.getParameterOr("start", Double.class, System.currentTimeMillis() + 1.0);
    return new Response<>(request.getId(), new Ping(millis));
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return AuthLevel.READ;
  }
}
