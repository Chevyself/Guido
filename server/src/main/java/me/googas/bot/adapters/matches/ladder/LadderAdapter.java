package me.googas.bot.adapters.matches.ladder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.matches.ladder.Ladder;
import me.googas.bot.core.matches.ladder.GuidoLadder;
import me.googas.commons.gson.adapters.JsonAdapter;

public class LadderAdapter implements JsonAdapter<Ladder> {
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
