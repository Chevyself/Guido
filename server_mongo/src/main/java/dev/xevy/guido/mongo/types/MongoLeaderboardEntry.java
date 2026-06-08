package dev.xevy.guido.mongo.types;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.stats.LeaderboardEntry;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public class MongoLeaderboardEntry implements LeaderboardEntry {
  @NonNull
  @Getter(onMethod_ = @BsonIgnore)
  public String display = "";

  @Getter(onMethod_ = @BsonIgnore)
  public double value = 0;

  @Getter(onMethod_ = @BsonIgnore)
  public double wins = 0;

  @Getter(onMethod_ = @BsonIgnore)
  public double loses = 0;

  @Override
  public String toString() {
    return "MongoLeaderboardEntry{" + "display='" + display + '\'' + ", value=" + value + '}';
  }
}
