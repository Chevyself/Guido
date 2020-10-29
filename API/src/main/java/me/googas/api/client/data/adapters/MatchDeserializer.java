package me.googas.api.client.data.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.googas.api.client.data.MatchImpl;
import me.googas.api.matches.Match;

import java.lang.reflect.Type;

/** Deserializes matches */
public class MatchDeserializer implements JsonDeserializer<Match> {
  @Override
  public Match deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, MatchImpl.class);
  }
}
