package com.starfishst.bot.server.responses.data;

import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.handlers.data.GuidoUnlinkedMember;
import com.starfishst.guido.api.data.AuthLevel;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.json.requests.data.MemberDataRequest;
import java.util.HashMap;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Responds to member data requests */
public class MemberDataResponse implements ResponseGiver<MemberData> {

  /** The loader to get the data of the member */
  @NotNull private final BotDataLoader loader;

  /**
   * Create the member data response giver
   *
   * @param loader the data loader to get the data from a member
   */
  public MemberDataResponse(@NotNull BotDataLoader loader) {
    this.loader = loader;
  }

  @Override
  public @Nullable Response<MemberData> getResponse(
      @NotNull Request<?> request, @NotNull Messenger messenger) {
    MemberDataRequest.RequestType type =
        MemberDataRequest.RequestType.valueOf(request.getParameter("type", String.class));
    long guildId = request.getParameterOr("guildId", Long.class, -1L);
    BotMember member;
    if (type == MemberDataRequest.RequestType.ID) {
      member = loader.getMemberData(request.getParameterOr("id", Long.class, -1L), guildId);
    } else {
      String key = request.getParameterOr("key", String.class, "0");
      String value = request.getParameterOr("value", String.class, "0");
      member = loader.getMemberByLink(guildId, key, key);
      if (member == null) {
        member = new GuidoUnlinkedMember(key, value, guildId, new HashSet<>(), new HashMap<>());
      }
    }
    BotMember copy = member.copy();
    if (!request.getParameterOr("stats", Boolean.class, true)) {
      copy.getStats().clear();
    }
    if (!request.getParameterOr("perms", Boolean.class, true)) {
      copy.getPermissions().clear();
    }
    return new Response<>(request.getId(), member);
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return AuthLevel.READ;
  }
}
