package dev.xevy.guido.mongo.types.mappers;

import dev.xevy.guido.mongo.types.MongoGuidoGuild;
import dev.xevy.guido.mongo.types.MongoRankRange;
import lombok.NonNull;
import me.googas.server.RankRange;

public final class RankRangeMapper {
  @NonNull
  public static RankRange fromDocument(@NonNull MongoGuidoGuild.RankRangeDocument document) {
    return new MongoRankRange(
        document.ladder, document.name, document.min, document.max, document.roleId);
  }

  @NonNull
  public static MongoGuidoGuild.RankRangeDocument toDocument(@NonNull MongoRankRange range) {
    MongoGuidoGuild.RankRangeDocument doc = new MongoGuidoGuild.RankRangeDocument();
    doc.ladder = range.getLadder();
    doc.name = range.getName();
    doc.min = range.getMin();
    doc.max = range.getMax();
    doc.roleId = range.getRoleId();
    return doc;
  }
}
