package com.starfishst.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.starfishst.bot.handlers.data.types.GuidoTeam;
import me.googas.api.matches.Team;
import java.lang.reflect.Type;

/** Deserializes teams */
public class TeamDeserializer implements JsonDeserializer<Team> {
  @Override
  public Team deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoTeam.class);
  }
}
