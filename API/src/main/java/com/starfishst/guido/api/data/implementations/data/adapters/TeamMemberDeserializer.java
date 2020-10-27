package com.starfishst.guido.api.data.implementations.data.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.starfishst.guido.api.data.implementations.data.TeamMemberImpl;
import com.starfishst.guido.api.data.matches.TeamMember;
import java.lang.reflect.Type;

/** Deserializes team member */
public class TeamMemberDeserializer implements JsonDeserializer<TeamMember> {
  @Override
  public TeamMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, TeamMemberImpl.class);
  }
}
