package dev.xevy.guido.mongo.types;

import java.util.UUID;
import lombok.NonNull;
import me.googas.api.stats.MinecraftStatsId;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsLinkableType;
import me.googas.api.utility.UUIDUtils;

public class MongoStatsId implements MinecraftStatsId {

  @NonNull private final Document document;

  public MongoStatsId(@NonNull Document document) {
    this.document = document;
  }

  @Override
  public @NonNull StatsLinkableType getLinkableType() {
    return StatsLinkableType.MINECRAFT;
  }

  @Override
  public @NonNull String getContext() {
    return this.document.context;
  }

  @Override
  public @NonNull UUID getLinkableId() {
    return this.document.linkableId;
  }

  public static class Document {
    @NonNull public String context = Stats.EMPTY_CONTEXT;
    @NonNull public UUID linkableId = UUIDUtils.EMPTY;
  }
}
