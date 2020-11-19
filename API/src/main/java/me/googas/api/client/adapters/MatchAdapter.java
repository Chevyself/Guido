package me.googas.api.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.SimpleMatch;
import me.googas.api.matches.Match;
import me.googas.commons.gson.adapters.JsonAdapter;

/** Deserializes matches */
public class MatchAdapter implements JsonAdapter<Match> {
  @Override
  public JsonElement serialize(Match src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src);
  }

  @Override
  public Match deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleMatch.class);
  }
}
