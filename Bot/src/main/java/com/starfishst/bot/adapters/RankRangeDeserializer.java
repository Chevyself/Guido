package com.starfishst.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.starfishst.bot.handlers.data.GuidoRankRange;
import com.starfishst.guido.api.data.RankRange;
import java.lang.reflect.Type;

/** Deserializes rank ranges */
public class RankRangeDeserializer implements JsonDeserializer<RankRange> {
  @Override
  public RankRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoRankRange.class);
  }
}
