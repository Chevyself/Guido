package me.googas.bot.adapters.matches.ladder;

import com.google.gson.*;
import java.lang.reflect.Type;
import me.googas.api.matches.ladder.Ladder;
import me.googas.bot.core.matches.ladder.GuidoLadder;

public class LadderAdapter implements JsonSerializer<Ladder>, JsonDeserializer<Ladder> {
  @Override
  public JsonElement serialize(Ladder src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src, GuidoLadder.class);
  }

  @Override
  public Ladder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoLadder.class);
  }
}
