package me.googas.api.adapters.matches.ladder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.matches.ladder.SimpleLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.commons.gson.adapters.JsonAdapter;

public class LadderDeserializer implements JsonDeserializer<Ladder> {

  @Override
  public Ladder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleLadder.class);
  }
}
