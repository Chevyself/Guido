package me.googas.api.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.SimpleTeam;
import me.googas.api.matches.Team;
import me.googas.commons.gson.adapters.JsonAdapter;

/** Deserializer for teams */
public class TeamAdapter implements JsonAdapter<Team> {

  @Override
  public JsonElement serialize(Team src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src);
  }

  @Override
  public Team deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleTeam.class);
  }
}
