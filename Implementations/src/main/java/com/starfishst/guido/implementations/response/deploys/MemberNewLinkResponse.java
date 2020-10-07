package com.starfishst.guido.implementations.response.deploys;

import com.starfishst.guido.api.data.AuthLevel;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Reads the requests of new links in members */
public class MemberNewLinkResponse implements ResponseGiver<Void> {

  @Override
  public @Nullable Response<Void> getResponse(
      @NotNull Request<?> request, @NotNull Messenger messenger) {
    long id = request.getParameterOr("id", Double.class, -1D).longValue();
    long guildId = request.getParameterOr("guildId", Double.class, -1D).longValue();
    String key = request.getParameter("key", String.class);
    String value = request.getParameter("key", String.class);
    return null;
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return null;
  }
}
