package com.starfishst.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.starfishst.bot.handlers.data.GuidoLadder;
import com.starfishst.guido.api.data.matches.Ladder;
import java.lang.reflect.Type;

/** Deserializes ladders */
public class LadderDeserializer implements JsonDeserializer<Ladder> {
  @Override
  public Ladder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoLadder.class);
  }
}
