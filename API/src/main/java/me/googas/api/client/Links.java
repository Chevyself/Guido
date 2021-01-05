package me.googas.api.client;

import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;

public class Links {

  @NonNull
  public static UUID getUuid(@NonNull LinkableInfo link) {
    if (link.getType() != LinkableType.MINECRAFT)
      throw new IllegalArgumentException(link + " is not a minecraft link");
    return UUIDUtils.untrim(link.getIdentification().getOr("uuid", String.class, ""));
  }
}
