package me.googas.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.RankRange;
import me.googas.bot.handlers.data.types.GuidoRankRange;
import me.googas.commons.gson.adapters.JsonAdapter;

/** Deserializes rank ranges */
public class RankRangeAdapter implements JsonAdapter<RankRange> {
  @Override
  public JsonElement serialize(RankRange src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src, GuidoRankRange.class);
  }

  @Override
  public RankRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoRankRange.class);
  }
}
