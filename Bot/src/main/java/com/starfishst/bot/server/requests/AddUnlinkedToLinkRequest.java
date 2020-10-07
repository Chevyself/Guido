package com.starfishst.bot.server.requests;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.implementations.messaging.Request;
import org.jetbrains.annotations.NotNull;

public class AddUnlinkedToLinkRequest extends Request<String> {

  /**
   * Request to add an unlinked member to a key to link in {@link
   * com.starfishst.bot.handlers.links.LinksHandler}
   *
   * @param guildId the id of the guild of the unlinked member
   * @param key the key of the unlinked member link
   * @param value the value of the unlinked member link
   */
  public AddUnlinkedToLinkRequest(long guildId, @NotNull String key, @NotNull String value) {
    super(
        "add-unlinked-to-link",
        Maps.builder("guildId", (Object) guildId)
            .append("key", key)
            .append("value", value)
            .build());
  }

  @Override
  public @NotNull Class<String> getClazz() {
    return String.class;
  }
}
