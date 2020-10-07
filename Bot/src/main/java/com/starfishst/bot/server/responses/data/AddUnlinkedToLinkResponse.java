package com.starfishst.bot.server.responses.data;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.handlers.links.LinksHandler;
import com.starfishst.guido.api.data.AuthLevel;
import com.starfishst.guido.api.data.UnlinkedMemberData;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The response to add a unlinked to link */
public class AddUnlinkedToLinkResponse implements ResponseGiver<String> {

  @Override
  public @NotNull AuthLevel getLevel() {
    return AuthLevel.READ_WRITE;
  }

  @Override
  public @Nullable Response<String> getResponse(
      @NotNull Request<?> request, @NotNull Messenger messenger) {
    BotMember member =
        Guido.getDataLoader()
            .getMemberByLink(
                request.getParameterOr("guildId", Double.class, -1D).longValue(),
                request.getParameterOr("key", String.class, "0"),
                request.getParameterOr("value", String.class, "0"));
    if (member instanceof UnlinkedMemberData) {
      return new Response<>(
          request.getId(),
          Guido.getHandler(LinksHandler.class).addToLink((UnlinkedMemberData) member));
    } else {
      return new Response<>(request.getId(), "error:already.linked");
    }
  }
}
