package dev.xevy.guido.mongo.types;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.NamedMinecraftLinkable;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public class MongoNamedMinecraftLinkable implements NamedMinecraftLinkable {
  @NonNull
  @Getter(onMethod_ = @BsonIgnore)
  public String nickname = "";
}
