package com.starfishst.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.starfishst.bot.handlers.data.types.GuidoTeamMember;
import me.googas.api.matches.TeamMember;
import java.lang.reflect.Type;

/** Deserializes team members */
public class TeamMemberAdapter implements JsonDeserializer<TeamMember> {

  @Override
  public TeamMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoTeamMember.class);
  }
}
