package me.googas.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.matches.Team;
import me.googas.bot.handlers.data.types.GuidoTeam;
import me.googas.commons.gson.adapters.JsonAdapter;

/** Adapts teams in json */
public class TeamAdapter implements JsonAdapter<Team> {
  @Override
  public JsonElement serialize(Team src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src, GuidoTeam.class);
  }

  @Override
  public Team deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoTeam.class);
  }
}
