package com.starfishst.bot.handlers.deploy.deployments;

import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;

/** Deployed when a member gets a new link */
public class MemberNewLink extends VoidRequest {

  /**
   * Create the request
   *
   * @param key the key of the link
   * @param value the value of the link
   * @param id the id of the member
   * @param guildId the id of the guild where the member is
   */
  public MemberNewLink(@NotNull String key, @NotNull String value, long id, long guildId) {
    super(
        "member-new-link",
        Maps.builder("key", (Object) key)
            .append("value", value)
            .append("id", id)
            .append("guildId", guildId)
            .build());
  }
}
