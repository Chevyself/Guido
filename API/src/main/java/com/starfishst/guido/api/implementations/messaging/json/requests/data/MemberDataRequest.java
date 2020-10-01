package com.starfishst.guido.api.implementations.messaging.json.requests.data;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.implementations.messaging.Request;
import org.jetbrains.annotations.NotNull;

/** Request the data of a member */
public class MemberDataRequest extends Request<MemberData> {

  /**
   * Requests the member data from a link
   *
   * @param guildId the id of the guild where the member is on
   * @param key the key of the link
   * @param value the value of the link
   * @param includeStats whether this should include the stats
   * @param includePerms whether it should include the perms of the user
   */
  public MemberDataRequest(
      long guildId,
      @NotNull String key,
      @NotNull String value,
      boolean includeStats,
      boolean includePerms) {
    super(
        "member-data",
        Maps.builder("type", (Object) RequestType.LINK)
            .append("guildId", guildId)
            .append("key", key)
            .append("value", value)
            .append("stats", includeStats)
            .append("perms", includePerms)
            .build());
  }

  /**
   * Requests the member data using its id
   *
   * @param guildId the id of the guild where the member is on
   * @param id the id of the member
   * @param includeStats whether the request should include the stats
   * @param includePerms whether it should include the perms of the user
   */
  public MemberDataRequest(long guildId, long id, boolean includeStats, boolean includePerms) {
    super(
        "member-data",
        Maps.builder("type", (Object) RequestType.ID)
            .append("guildId", guildId)
            .append("id", id)
            .append("stats", includeStats)
            .append("perms", includePerms)
            .build());
  }

  @Override
  public @NotNull Class<MemberData> getClazz() {
    return MemberData.class;
  }

  /** The type of request */
  public enum RequestType {
    /** Requesting using a link of the member */
    LINK,
    /** Requesting using the id of the member */
    ID
  }
}
